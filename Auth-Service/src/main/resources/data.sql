DROP TABLE IF EXISTS auth_users;
CREATE TABLE IF NOT EXISTS auth_users(
    id UUID,
    email VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255),
    role VARCHAR(255)
);

INSERT INTO auth_users(id,email,password,role) values('c9b1c678-344d-4a71-8f0a-1e6dc839e3f2','kkr@gmail.com','$2a$12$NifyCgmne5.LBpnco9DHF.ldNuD8B0n7CpIl3g2ytvhkRT5HUzF/y','ADMIN') ON CONFLICT (email) DO NOTHING;