-- Nhân vật kiểm thử tính năng theo dữ liệu bản phát hành đang dùng trong server.
-- Tài khoản: bossfull / hoang123
-- Nhân vật: BossFull (Chiến binh, bắt đầu từ cấp 1)
-- Có đủ 12 pet trưởng thành, đủ các ô trang bị, hành trang 126 ô,
-- mề đay chiến binh phẩm Cam thiên về sát thương Lửa +9,
-- đúng 10.000.000 vàng và 10.000 ngọc tại thời điểm chạy script.

SET NAMES utf8mb4;
START TRANSACTION;

INSERT INTO `account`
    (`user`, `pass`, `char`, `status`, `lock`, `coin`, `phone`, `ip`, `tiennap`, `pass2`, `tongnap`)
VALUES
    ('bossfull', 'hoang123', '["BossFull"]', 1, 0, 0, 0, '0', 0, '0', 0)
ON DUPLICATE KEY UPDATE
    `pass` = VALUES(`pass`),
    `char` = VALUES(`char`),
    `status` = 1,
    `lock` = 0;

-- Dùng nhân vật kiểm thử chuẩn đã được server dựng để lấy đủ cấu trúc dữ liệu.
INSERT INTO `player` (
    `name`, `body`, `clazz`, `maxbag`, `diemdanh`, `typeexp`, `level`, `exp`, `site`,
    `dquest`, `rms_save`, `nvtt`, `tinhtu_material`, `friend`, `eff`, `enemies`, `skill`,
    `item3`, `item4`, `item5`, `item7`, `itembox3`, `itembox4`, `itembox5`, `itembox7`,
    `itemwear`, `pet`, `vang`, `kimcuong`, `tiemnang`, `kynang`, `point1`, `point2`,
    `point3`, `point4`, `giftcode`, `date`, `medal_create_material`, `point_active`,
    `chucphuc`, `hieuchien`, `id_name`, `chuyensinh`, `tutien`, `pointarena`, `dropnlmd`, `detu`
)
SELECT
    'BossFull', `body`, 0, 126, `diemdanh`, `typeexp`, 1, 0, `site`,
    `dquest`, `rms_save`, `nvtt`, `tinhtu_material`, '[]', `eff`, '[]', `skill`,
    '[]', `item4`, `item5`, `item7`, `itembox3`, `itembox4`, `itembox5`, `itembox7`,
    `itemwear`, `pet`, 10000000, 10000, `tiemnang`, `kynang`, `point1`, `point2`,
    `point3`, `point4`, '["viet-hoang-profile-v4"]', DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s'),
    `medal_create_material`, `point_active`, `chucphuc`, 0, -1, `chuyensinh`, `tutien`,
    0, `dropnlmd`, `detu`
FROM `player`
WHERE `name` = 'lyyeu1'
  AND NOT EXISTS (SELECT 1 FROM `player` WHERE `name` = 'BossFull');

UPDATE `player` AS dst
JOIN `player` AS src ON src.`name` = 'lyyeu1'
SET dst.`body` = src.`body`,
    dst.`clazz` = 0,
    dst.`maxbag` = 126,
    dst.`typeexp` = src.`typeexp`,
    dst.`level` = 1,
    dst.`exp` = 0,
    dst.`site` = src.`site`,
    dst.`dquest` = src.`dquest`,
    dst.`rms_save` = src.`rms_save`,
    dst.`nvtt` = src.`nvtt`,
    dst.`tinhtu_material` = src.`tinhtu_material`,
    dst.`skill` = src.`skill`,
    dst.`item4` = src.`item4`,
    dst.`item5` = src.`item5`,
    dst.`item7` = src.`item7`,
    dst.`itembox3` = src.`itembox3`,
    dst.`itembox4` = src.`itembox4`,
    dst.`itembox5` = src.`itembox5`,
    dst.`itembox7` = src.`itembox7`,
    dst.`itemwear` = src.`itemwear`,
    dst.`pet` = src.`pet`,
    dst.`tiemnang` = src.`tiemnang`,
    dst.`kynang` = src.`kynang`,
    dst.`point1` = src.`point1`,
    dst.`point2` = src.`point2`,
    dst.`point3` = src.`point3`,
    dst.`point4` = src.`point4`,
    dst.`giftcode` = '["viet-hoang-profile-v4"]',
    dst.`medal_create_material` = src.`medal_create_material`,
    dst.`point_active` = src.`point_active`,
    dst.`tutien` = src.`tutien`,
    dst.`vang` = 10000000,
    dst.`kimcuong` = 10000,
    dst.`friend` = '[]',
    dst.`enemies` = '[]',
    dst.`hieuchien` = 0,
    dst.`pointarena` = 0,
    -- color=4: phẩm Cam; tier=9: +9; option=9: sát thương Lửa.
    dst.`item3` = '[[4587,4,16,150,954,4,0,0,9,[[9,900],[14,600],[18,600],[23,100],[24,100],[25,100],[26,100],[27,5000],[40,300]],0]]'
WHERE dst.`name` = 'BossFull';

INSERT INTO `player_full_pet_reward` (`player_name`, `unlock_level`, `pet_json`, `enabled`)
SELECT 'BossFull', 10, `pet`, 1
FROM `player`
WHERE `name` = 'BossFull'
ON DUPLICATE KEY UPDATE
    `unlock_level` = VALUES(`unlock_level`),
    `pet_json` = VALUES(`pet_json`),
    `enabled` = 1;

COMMIT;

SELECT
    a.`user`, p.`name`, p.`clazz`, p.`level`, p.`maxbag`, p.`vang`, p.`kimcuong`,
    JSON_LENGTH(p.`pet`) AS `pet_count`,
    JSON_LENGTH(p.`itemwear`) AS `equipped_count`,
    JSON_EXTRACT(p.`item3`, '$[0][0]') AS `medal_id`,
    JSON_EXTRACT(p.`item3`, '$[0][5]') AS `medal_color`,
    JSON_EXTRACT(p.`item3`, '$[0][8]') AS `medal_tier`
FROM `account` a
JOIN `player` p ON JSON_CONTAINS(a.`char`, JSON_QUOTE(p.`name`))
WHERE a.`user` = 'bossfull';
