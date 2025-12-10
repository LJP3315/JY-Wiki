-- 创建数据库（如果需要）
CREATE DATABASE IF NOT EXISTS jycms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE jycms;

-- 用户表
DROP TABLE IF EXISTS `User`;
CREATE TABLE `User` (
                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                        `username` VARCHAR(50) NOT NULL UNIQUE,
                        `password` VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试用户（注意：示例中密码为明文 "123456"，生产环境请使用哈希）
INSERT INTO `User` (`username`, `password`) VALUES ('admin', '123456');

-- 人物表
DROP TABLE IF EXISTS `Character`;
CREATE TABLE `Character` (
                             `id` INT AUTO_INCREMENT PRIMARY KEY,
                             `name` VARCHAR(100) NOT NULL,
                             `novel_name` VARCHAR(100),
                             `description_short` VARCHAR(255),
                             `description_full` TEXT,
                             `image_url` VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 武功表
DROP TABLE IF EXISTS `MartialArt`;
CREATE TABLE `MartialArt` (
                              `id` INT AUTO_INCREMENT PRIMARY KEY,
                              `char_id` INT NOT NULL,
                              `art_name` VARCHAR(100) NOT NULL,
                              `art_description` TEXT,
                              CONSTRAINT fk_char FOREIGN KEY (`char_id`) REFERENCES `Character`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 1. 新建小说表
CREATE TABLE `Novel` (
                         `id` INT PRIMARY KEY AUTO_INCREMENT,
                         `title` VARCHAR(255) NOT NULL,
                         `publication_year` INT
);

-- 2. 修改角色表结构
-- (注意：生产环境中需要先迁移数据，这里假设是新项目或您已处理好数据备份)
-- 如果 Character 表中已有数据，请先创建 Novel 数据，并将对应的 ID 填入 character.novel_id
ALTER TABLE `Character` ADD COLUMN `novel_id` INT;
ALTER TABLE `Character` ADD CONSTRAINT `fk_novel` FOREIGN KEY (`novel_id`) REFERENCES `Novel`(`id`);
-- 删除旧的冗余字段
ALTER TABLE `Character` DROP COLUMN `novel_name`;

-- 3. 新建收藏表
CREATE TABLE `Collection` (
                              `id` INT PRIMARY KEY AUTO_INCREMENT,
                              `char_id` INT NOT NULL,
    -- 唯一约束，防止重复收藏同一个人
                              UNIQUE KEY `uk_char` (`char_id`),
                              FOREIGN KEY (`char_id`) REFERENCES `Character`(`id`)
);

create table martialart(
                           `id` INT AUTO_INCREMENT PRIMARY KEY,
                           `art_name` VARCHAR(100) NOT NULL
);

CREATE TABLE `CharacterArt` (
                                `id` INT PRIMARY KEY AUTO_INCREMENT,
                                `char_id` INT NOT NULL,  -- 人物ID (外键)
                                `art_id` INT NOT NULL,   -- 武功ID (外键)
                                `art_description` TEXT,  -- 针对该人物的武功描述 (关系属性)

    -- 复合唯一约束：确保同一个人不会重复关联同一武功
                                UNIQUE KEY `uk_char_art` (`char_id`, `art_id`),

    -- 外键约束：关联 Character 表
                                FOREIGN KEY (`char_id`) REFERENCES `Character`(`id`),

    -- 外键约束：关联 MartialArt 表
                                FOREIGN KEY (`art_id`) REFERENCES `MartialArt`(`id`)
);

create table CharacterRelation (
    id int primary key auto_increment,
    char_id_a int not null,
    char_id_b int not null,
    relation_type varchar(50) not null,
    description text null,

    unique key uk_char_relation (char_id_a, char_id_b, relation_type),
    foreign key (char_id_a) references `Character`(id),
    foreign key (char_id_b) references `Character`(id)
);

-- 人物基础信息视图
create view V_Character_Base as (
select c.id, c.novel_id, c.name, c.description_short, c.description_full, c.image_url, n.title as novel_title
from `Character` c left join Novel n on c.novel_id = n.id);

-- 人物武功详情视图
CREATE VIEW V_Character_Arts AS
SELECT
    ca.id, ca.char_id, ca.art_description,
    ma.art_name
FROM `CharacterArt` ca
         JOIN `MartialArt` ma ON ca.art_id = ma.id;

-- 人物关系详情视图
CREATE VIEW V_Character_Relations AS
SELECT
    cr.id, cr.char_id_a, cr.char_id_b, cr.relation_type, cr.description,
    c2.name AS related_char_name
FROM `CharacterRelation` cr
         JOIN `Character` c2 ON cr.char_id_b = c2.id;

-- 收藏列表视图
CREATE VIEW V_Collection_Details AS
SELECT
    col.char_id AS id, -- 使用 char_id 作为 ID
    c.novel_id, c.name, c.description_short, c.image_url,
    n.title AS novel_title, c.description_full
FROM `Collection` col
         JOIN `Character` c ON col.char_id = c.id
         LEFT JOIN `Novel` n ON c.novel_id = n.id;