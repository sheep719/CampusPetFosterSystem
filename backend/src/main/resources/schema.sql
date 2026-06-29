CREATE TABLE IF NOT EXISTS sys_user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_code VARCHAR(20) NOT NULL,
    phone VARCHAR(20),
    enabled INTEGER DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pet_owner_profile (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL UNIQUE,
    student_no VARCHAR(50),
    name VARCHAR(50),
    school VARCHAR(100),
    major VARCHAR(100),
    dorm VARCHAR(100),
    contact_phone VARCHAR(20),
    photo_url VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS pet_caregiver_profile (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL UNIQUE,
    name VARCHAR(50),
    address VARCHAR(200),
    distance_to_school_km DOUBLE,
    phone VARCHAR(20),
    experience_years INTEGER DEFAULT 0,
    has_own_pets INTEGER DEFAULT 0,
    care_types VARCHAR(100),
    bio TEXT,
    avatar_url VARCHAR(255),
    avg_rating DOUBLE DEFAULT 0.0,
    total_care_count INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS pet (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id INTEGER NOT NULL,
    pet_name VARCHAR(50) NOT NULL,
    species VARCHAR(20),
    breed VARCHAR(50),
    gender VARCHAR(10),
    age INTEGER,
    photo_url VARCHAR(255),
    health_note TEXT,
    diet_note TEXT,
    vaccine_status VARCHAR(50),
    deworm_status VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS foster_location (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    caregiver_id INTEGER NOT NULL,
    location_name VARCHAR(100) NOT NULL,
    address VARCHAR(200),
    distance_km DOUBLE,
    capacity INTEGER DEFAULT 1,
    accepted_species VARCHAR(100),
    care_period VARCHAR(50),
    cost_desc VARCHAR(200),
    available INTEGER DEFAULT 1,
    environment_photos VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS foster_application (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    pet_id INTEGER NOT NULL,
    location_id INTEGER,
    caregiver_id INTEGER NOT NULL,
    owner_id INTEGER NOT NULL,
    start_time DATETIME,
    expected_end_time DATETIME,
    actual_start_time DATETIME,
    actual_end_time DATETIME,
    care_note TEXT,
    emergency_contact VARCHAR(100),
    status VARCHAR(20) DEFAULT 'pending',
    reject_reason TEXT,
    rating INTEGER,
    review_text TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS care_diary (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    application_id INTEGER NOT NULL,
    caregiver_id INTEGER NOT NULL,
    diary_date DATE NOT NULL,
    content TEXT,
    photo_url VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS handover_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    application_id INTEGER NOT NULL,
    handover_type VARCHAR(20),
    handover_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    from_person VARCHAR(50),
    to_person VARCHAR(50),
    remark TEXT,
    photo_url VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS notification (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    type VARCHAR(20),
    title VARCHAR(100),
    content TEXT,
    link_url VARCHAR(255),
    read_status BOOLEAN DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    read_time DATETIME
);

INSERT OR IGNORE INTO sys_user (username, password, role_code, phone, enabled, create_time) VALUES
('admin', '$2a$10$rD3Z7FWH4fID9VxQxQv1HOYb6qLp0L3M7D4E9F3H5I6J7K8L9M0N', 'admin', '13800138000', 1, datetime('now')),
('student001', '$2a$10$rD3Z7FWH4fID9VxQxQv1HOYb6qLp0L3M7D4E9F3H5I6J7K8L9M0N', 'owner', '13900139001', 1, datetime('now')),
('caregiver001', '$2a$10$rD3Z7FWH4fID9VxQxQv1HOYb6qLp0L3M7D4E9F3H5I6J7K8L9M0N', 'caregiver', '13900139002', 1, datetime('now'));

INSERT OR IGNORE INTO pet_owner_profile (user_id, student_no, name, school, major, dorm, contact_phone) VALUES
(2, '2024001', '张三', 'XX大学', '计算机科学与技术', '男生宿舍1号楼301', '13900139001');

INSERT OR IGNORE INTO pet_caregiver_profile (user_id, name, address, distance_to_school_km, phone, experience_years, has_own_pets, care_types, bio, avg_rating, total_care_count) VALUES
(3, '李阿姨', 'XX小区3号楼201室', 1.5, '13900139002', 5, 1, '["cat","dog"]', '喜欢小动物，有多年养猫经验', 4.8, 12);

INSERT OR IGNORE INTO pet (owner_id, pet_name, species, breed, gender, age, health_note, diet_note, vaccine_status, deworm_status) VALUES
(2, '咪咪', 'cat', '英短', 'female', 2, '身体健康，性格温顺', '每天喂食两次，喜欢吃鱼罐头', '已完成疫苗接种', '已驱虫');

INSERT OR IGNORE INTO foster_location (caregiver_id, location_name, address, distance_km, capacity, accepted_species, care_period, cost_desc, available) VALUES
(3, '李阿姨家', 'XX小区3号楼201室', 1.5, 3, '["cat","dog"]', '全天', '免费', 1);
