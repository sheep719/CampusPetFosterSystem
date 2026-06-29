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
('admin', '$2a$10$rD3Z7FWH4fID9VxQxQv1HOYb6qLp0L3M7D4E9F3H5I6J7K8L9M0N', 'admin', '13800138000', 1, '2024-01-01 00:00:00'),
('student001', '$2a$10$rD3Z7FWH4fID9VxQxQv1HOYb6qLp0L3M7D4E9F3H5I6J7K8L9M0N', 'owner', '13900139001', 1, '2024-01-02 00:00:00'),
('caregiver001', '$2a$10$rD3Z7FWH4fID9VxQxQv1HOYb6qLp0L3M7D4E9F3H5I6J7K8L9M0N', 'caregiver', '13900139002', 1, '2024-01-03 00:00:00');
