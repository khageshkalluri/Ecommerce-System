package LocalStack;

import org.apache.commons.logging.Log;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.msk.CfnCluster.BrokerNodeGroupInfoProperty;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.route53.CfnHealthCheck;
import software.amazon.awscdk.services.route53.CfnHealthCheck.HealthCheckConfigProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocalStackBuilder extends Stack {
    private final Vpc vpc;
    private final DatabaseInstance authDatabaseInstance;
    private final DatabaseInstance usersDatabaseInstance;
    private final DatabaseInstance analyticsDatabaseInstance;
    private final CfnHealthCheck authDBHealthCheck;
    private final CfnHealthCheck usersDBHealthCheck;
    private final CfnCluster kafkacluster;
    private final Cluster ecsCluster;
    private final FargateService authFargateService;
    private final FargateService notificationFargateService;
    private final FargateService analyticsFargateService;
    private final FargateService usersFargateService;
    public LocalStackBuilder(final App scope, final String id, final StackProps props) {
       super(scope,id,props);
       this.vpc=createVpc();
       this.authDatabaseInstance=createDatabase("AuthDatabase","auth-database");
       this.usersDatabaseInstance=createDatabase("UsersDatabase","users-database");
       this.authDBHealthCheck = createHealthCheck(authDatabaseInstance,"AuthDBHealthCheck");
       this.usersDBHealthCheck = createHealthCheck(usersDatabaseInstance,"UsersDBHealthCheck");
       this.analyticsDatabaseInstance=createDatabase("AnalyticsDatabase","analytics-database");
       this.kafkacluster=createMskCluster("KAFKA");
       this.ecsCluster = createEcsCluster();
       this.authFargateService=createFargateService("AuthService","auth-service",List.of(2000),authDatabaseInstance,
               Map.of("JWT_SECRET","YYRgPrmBddhIVaSm9yYZ2GLKZ5E36mh1rI0tJ0e0zJY=","SPRING_DATASOURCE_DRIVER_CLASS_NAME","org.postgresql.Driver"),"auth-database");
       this.authFargateService.getNode().addDependency(this.authDBHealthCheck);
       this.authFargateService.getNode().addDependency(this.authDatabaseInstance);
       this.notificationFargateService=createFargateService("NotificationService","notificationservice",List.of(8000,8001),
               null,null,null);
       this.analyticsFargateService=createFargateService("AnalyticsService","analytics", List.of(1000),analyticsDatabaseInstance,Map.of("SPRING_DATASOURCE_DRIVER_CLASS_NAME","org.postgresql.Driver"),"analytics-database");
       this.analyticsFargateService.getNode().addDependency(this.kafkacluster);
       this.usersFargateService=createFargateService("UsersService","users-docker",List.of(4000),usersDatabaseInstance,
               Map.of("NOTIFICATION_SERVER_ADDRESS","host.docker.internal","NOTIFICATION_SERVER_PORT","8001","KAFKA_TOPIC","users",
                       "SPRING_DATASOURCE_DRIVER_CLASS_NAME","org.postgresql.Driver"),"users-database");
       this.usersFargateService.getNode().addDependency(this.usersDBHealthCheck);
       this.usersFargateService.getNode().addDependency(this.usersDatabaseInstance);
       this.usersFargateService.getNode().addDependency(this.notificationFargateService);
       this.usersFargateService.getNode().addDependency(this.kafkacluster);
       createApplicationLoadBalancedFargateService();
    }

    private Vpc createVpc() {
        return Vpc.Builder.
                create(this,"EcommerceVPC")
                .maxAzs(2)
                .vpcName("EcommerceVPC")
                .build();
    }

    private DatabaseInstance createDatabase(String id,String databaseName) {
        return DatabaseInstance.Builder.create(this,id)
                .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder().version(PostgresEngineVersion.VER_17).build()))
                .vpc(vpc)
                .instanceType(InstanceType.of(InstanceClass.T2, InstanceSize.MICRO))
                .allocatedStorage(20)
                .credentials(Credentials.fromGeneratedSecret("kkr"))
                .databaseName(databaseName)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
    }

    private CfnHealthCheck createHealthCheck(DatabaseInstance databaseInstance,String id) {
        return CfnHealthCheck.Builder.create(this,id)
                .healthCheckConfig(HealthCheckConfigProperty.builder()
                                .type("TCP")
                                .ipAddress(databaseInstance.getDbInstanceEndpointAddress())
                                .port(Token.asNumber(databaseInstance.getDbInstanceEndpointPort()))
                                        .failureThreshold(3)
                                                .requestInterval(30).build()).build();

    }

    private CfnCluster createMskCluster(String id) {
        return CfnCluster.Builder.create(this,id)
                .clusterName("Kafka-cluster")
                .numberOfBrokerNodes(2)
                .kafkaVersion("2.8.0")
                .brokerNodeGroupInfo(BrokerNodeGroupInfoProperty.builder()
                        .instanceType("kafka.t3.small")
                        .clientSubnets((vpc.getPrivateSubnets().stream().map(ISubnet::getSubnetId).collect(Collectors.toList())))
                        .brokerAzDistribution("DEFAULT")
                        .build()).build();
    }

    private Cluster createEcsCluster() {
        return Cluster.Builder.create(this,"ECS-CLUSTER")
                .vpc(vpc)
                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder().name("ecommerce.org").build()).build();
    }

    private FargateService createFargateService(String id, String imagename, List<Integer> ports, DatabaseInstance databaseInstance, Map<String,String> properties,String dbname) {

        TaskDefinition taskDefinition =TaskDefinition.Builder.create(this,id+"Task").compatibility(Compatibility.FARGATE)
                        .cpu(String.valueOf(256)).memoryMiB(String.valueOf(512)).build();

        ContainerDefinitionOptions.Builder containerDefinitionOptions = ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry(imagename))
                .portMappings(ports.stream().map(port->PortMapping.builder()
                        .hostPort(port)
                        .containerPort(port)
                        .protocol(Protocol.TCP)
                        .build()).collect(Collectors.toList()))
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                .logGroup(LogGroup.Builder.create(this,id+"LogGroup")
                                  .logGroupName("ecs logs "+id)
                                        .removalPolicy(RemovalPolicy.DESTROY)
                                        .retention(RetentionDays.ONE_DAY).
                                        build())
                                .streamPrefix(imagename)
                        .build()));

        Map<String,String> env_variables=new HashMap<>();
        env_variables.put("SPRING_KAFKA_BOOTSTRAP_SERVERS","localhost.localstack.cloud:4510, localhost.localstack.cloud:4511, localhost.localstack.cloud:4512");
        if(properties!=null) {
            env_variables.putAll(properties);
        }

        if(databaseInstance!=null) {
            env_variables.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://%s:%s/%s".formatted(
                    databaseInstance.getDbInstanceEndpointAddress(), databaseInstance.getDbInstanceEndpointPort(),
                    dbname
            ));
            env_variables.put("SPRING_DATASOURCE_USERNAME", "kkr");
            env_variables.put("SPRING_DATASOURCE_PASSWORD", databaseInstance.getSecret().secretValueFromJson("password").toString());
            env_variables.put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
            env_variables.put("SPRING_SQL_INIT_MODE", "always");
            env_variables.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT", "60000");
        }
        containerDefinitionOptions.environment(env_variables);
        taskDefinition.addContainer(id+"Container",containerDefinitionOptions.build());
        return FargateService.Builder.create(this,id)
                .cluster(ecsCluster)
                .taskDefinition(taskDefinition)
                .assignPublicIp(false)
                .serviceName(imagename)
                .build();


    }

    public ApplicationLoadBalancedFargateService createApplicationLoadBalancedFargateService() {
        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder.create(this,"APIGATEWAYGROUP")
                .cpu((256))
                .memoryLimitMiB((512))
                .build();

        Map<String,String> env_variables=new HashMap<>();
        env_variables.put("AUTH_SERVICE_URL", "http://host.docker.internal:2000");
        env_variables.put("SPRING_PROFILES_ACTIVE", "prod");

        ContainerDefinitionOptions containerDefinitionOptions = ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry("api-gateway"))
                .environment(env_variables)
                .portMappings(List.of(6004).stream().map(port->PortMapping.builder().containerPort(port)
                        .hostPort(port)
                        .protocol(Protocol.TCP)
                        .build()).collect(Collectors.toList()))
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                .streamPrefix("api-gateway")
                                .logGroup(LogGroup.Builder.create(this,"APIGATEWAYGROUPlogger")
                                        .logGroupName("ECS-GATEWAY")
                                        .removalPolicy(RemovalPolicy.DESTROY)
                                        .retention(RetentionDays.ONE_DAY)
                                        .build())
                        .build()))
                .build();

        taskDefinition.addContainer("api-gateway",containerDefinitionOptions);
        return ApplicationLoadBalancedFargateService.Builder.create(this,"APIGATEWAYGROUPFargateService")
                .assignPublicIp(false)
                .serviceName("api-gateway")
                .cluster(ecsCluster)
                .taskDefinition(taskDefinition)
                .healthCheckGracePeriod(Duration.seconds(60))
                .build();
    }

    public static void main(final String[] args) {
        App app=new App(AppProps.builder().outdir("./out.dir").build());
        StackProps props=StackProps.builder()
                .synthesizer(BootstraplessSynthesizer.Builder.create().build())
                .build();
        new LocalStackBuilder(app,"localstack",props);
        app.synth();
        System.out.printf("Synthesizing ...");
    }
}
