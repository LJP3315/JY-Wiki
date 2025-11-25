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

-- 插入部分金庸小说人物和武功（至少 5 条人物和若干武功）
INSERT INTO `Character` (`name`, `novel_name`, `description_short`, `description_full`, `image_url`) VALUES
                                                                                                         ('郭靖', '射雕英雄传', '忠厚老实，武艺高强', '郭靖，蒙古与汉族混血，自幼习武，为人忠厚正直，后来与黄蓉共闯江湖。', 'images/guojing.jpg'),
                                                                                                         ('黄蓉', '射雕英雄传', '机智聪颖，谋略过人', '黄蓉，桃花岛主黄药师之女，聪明伶俐，足智多谋，是郭靖的挚爱与搭档。', 'images/huangrong.jpg'),
                                                                                                         ('令狐冲', '笑傲江湖', '豁达不羁，剑法绝妙', '令狐冲，华山派弟子，性格潇洒不羁，与任盈盈有深厚情谊。', 'images/linghuchong.jpg'),
                                                                                                         ('张无忌', '倚天屠龙记', '为人宽厚，武学天赋高', '张无忌，明教教主，机缘巧合习得多种绝学，为人仁慈，颇具领袖气质。', 'images/zhangwuji.jpg'),
                                                                                                         ('杨过', '神雕侠侣', '性情刚烈，感情深沉', '杨过，出身曲折，与小龙女感情深厚，武艺奇高。', 'images/yangguo.jpg');

-- 为每个人物添加若干武功（MartialArt）
INSERT INTO `MartialArt` (`char_id`, `art_name`, `art_description`) VALUES
                                                                        (1, '降龙十八掌', '以力破巧，掌力浑厚，攻势刚猛，为丐帮至高掌法之一。'),
                                                                        (1, '九阴真经（部分）', '内功、拳术与奇门遁甲相结合的绝学（此处代表郭靖所学之部分）。'),
                                                                        (2, '打狗棒法（悟空棒法）', '桃花岛与丐帮技艺交流影响，且黄蓉善于变化陷阱与机关。'),
                                                                        (3, '独孤九剑', '以无招胜有招的剑法，速度与取位为主。'),
                                                                        (3, '吸星大法（部分）', '（令狐冲在某些剧情被牵连，不同版本描述不一，此处仅示例）'),
                                                                        (4, '乾坤大挪移', '以巧妙的身法和内力运转化解对方攻势并反制。'),
                                                                        (4, '明教剑法', '明教特有的剑法传承与招式融合。'),
                                                                        (5, '玉女素心剑', '轻灵飘逸，讲究以巧取胜。'),
                                                                        (5, '黯然销魂掌', '情感与招式结合的内劲型掌法。');

-- 说明：请将 images 文件夹放在程序运行目录下并放入对应图片（可用占位图片）。
