-- HSO: tao tai khoan/nhan vat moi va tu mo khoa full pet khi dat level.
-- MySQL 8.x, database mac dinh: hso2.
--
-- CHI CAN SUA 5 BIEN BEN DUOI ROI CHAY TOAN BO FILE.
SET @account_user = 'lyyeu1';
SET @account_pass = 'hoang123';
SET @player_name = 'lyyeu1';
SET @player_class = 0;  -- 0, 1, 2 hoac 3
SET @unlock_level = 10; -- level dat moc de tu nhan full pet

USE `hso2`;

-- 12 pet hop le theo client.Pet#get_pet. Tat ca cap 30, da tien hoa toi da,
-- 10.000 diem tiem nang/pet va option dac trung dat nguong toi da.
SET @full_pet_json = '[
  [30,1,3,3,0,0,300,2500,2500,2500,2500,10000,0,1,1,0,[[23,100,0],[24,100,0],[25,100,0],[26,100,0],[47,500,500],[4,2000,2000]]],
  [30,0,6,3,0,0,300,2500,2500,2500,2500,10000,0,0,1,0,[[23,100,0],[24,100,0],[25,100,0],[26,100,0],[44,300,300],[45,5000,5000],[1,2000,2000]]],
  [30,2,9,3,0,0,300,2500,2500,2500,2500,10000,0,0,1,0,[[23,100,0],[24,100,0],[25,100,0],[26,100,0],[49,100,100],[48,4500,4500],[0,2000,2000]]],
  [30,3,12,3,0,0,300,2500,2500,2500,2500,10000,0,0,1,0,[[23,100,0],[24,100,0],[25,100,0],[26,100,0],[46,2000,2000],[48,4500,4500],[3,2000,2000]]],
  [30,4,15,3,0,0,300,2500,2500,2500,2500,10000,0,0,1,0,[[23,100,0],[24,100,0],[25,100,0],[26,100,0],[48,4500,4500],[49,100,100],[2,2000,2000]]],
  [30,5,18,3,0,0,300,2500,2500,2500,2500,10000,0,0,1,0,[[23,100,0],[24,100,0],[25,100,0],[26,100,0],[48,4500,4500],[97,2300,2300],[98,4500,4500],[2,2000,2000]]],
  [30,7,24,3,0,0,300,2500,2500,2500,2500,10000,0,0,1,0,[[23,100,0],[24,100,0],[25,100,0],[26,100,0],[67,2350,2350],[113,500,500],[114,3,3],[1,2000,2000]]],
  [30,8,27,3,0,0,300,2500,2500,2500,2500,10000,0,0,1,0,[[23,100,0],[24,100,0],[25,100,0],[26,100,0],[48,4500,4500],[80,200,200],[85,150,150],[86,1500,1500],[114,3,3],[0,2000,2000]]],
  [30,9,30,3,0,0,300,2500,2500,2500,2500,10000,0,0,1,0,[[23,100,0],[24,100,0],[25,100,0],[26,100,0],[48,4500,4500],[80,200,200],[85,150,150],[86,1500,1500],[114,3,3],[1,2000,2000]]],
  [30,11,35,3,0,0,300,2500,2500,2500,2500,10000,0,0,1,0,[[23,100,0],[24,100,0],[25,100,0],[26,100,0],[48,4500,4500],[80,200,200],[114,3,3],[2,2000,2000]]],
  [30,12,39,3,0,0,300,2500,2500,2500,2500,10000,0,0,1,0,[[23,100,0],[24,100,0],[25,100,0],[26,100,0],[48,4500,4500],[80,200,200],[114,3,3],[3,2000,2000]]],
  [30,13,42,3,0,0,300,2500,2500,2500,2500,10000,0,0,1,0,[[23,100,0],[24,100,0],[25,100,0],[26,100,0],[48,4500,4500],[80,200,200],[85,150,150],[86,1500,1500],[114,3,3],[1,2000,2000]]]
]';

CREATE TABLE IF NOT EXISTS `player_full_pet_reward` (
  `player_name` varchar(255) NOT NULL,
  `unlock_level` int NOT NULL,
  `pet_json` longtext NOT NULL,
  `enabled` tinyint NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`player_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Trigger chi tac dong den nhan vat co ten trong player_full_pet_reward.
-- Neu server save [] ngay sau khi dat moc, trigger van giu pet cho toi khi dang nhap lai.
DROP TRIGGER IF EXISTS `trg_player_full_pet_before_insert`;
DROP TRIGGER IF EXISTS `trg_player_full_pet_before_update`;

DELIMITER $$

CREATE TRIGGER `trg_player_full_pet_before_insert`
BEFORE INSERT ON `player`
FOR EACH ROW
BEGIN
  DECLARE v_unlock_level int DEFAULT NULL;
  DECLARE v_pet_json longtext DEFAULT NULL;

  SELECT `unlock_level`, `pet_json`
    INTO v_unlock_level, v_pet_json
    FROM `player_full_pet_reward`
   WHERE `player_name` = NEW.`name` AND `enabled` = 1
   LIMIT 1;

  IF v_unlock_level IS NOT NULL AND NEW.`level` >= v_unlock_level THEN
    IF NEW.`pet` IS NULL OR JSON_VALID(NEW.`pet`) = 0 THEN
      SET NEW.`pet` = v_pet_json;
    ELSEIF JSON_LENGTH(NEW.`pet`) = 0 THEN
      SET NEW.`pet` = v_pet_json;
    END IF;
  END IF;
END$$

CREATE TRIGGER `trg_player_full_pet_before_update`
BEFORE UPDATE ON `player`
FOR EACH ROW
BEGIN
  DECLARE v_unlock_level int DEFAULT NULL;
  DECLARE v_pet_json longtext DEFAULT NULL;

  SELECT `unlock_level`, `pet_json`
    INTO v_unlock_level, v_pet_json
    FROM `player_full_pet_reward`
   WHERE `player_name` = NEW.`name` AND `enabled` = 1
   LIMIT 1;

  IF v_unlock_level IS NOT NULL AND NEW.`level` >= v_unlock_level THEN
    IF NEW.`pet` IS NULL OR JSON_VALID(NEW.`pet`) = 0 THEN
      SET NEW.`pet` = v_pet_json;
    ELSEIF JSON_LENGTH(NEW.`pet`) = 0 THEN
      SET NEW.`pet` = v_pet_json;
    END IF;
  END IF;
END$$

DROP PROCEDURE IF EXISTS `create_hso_account_full_pet`$$
CREATE PROCEDURE `create_hso_account_full_pet`(
  IN p_user varchar(255),
  IN p_pass varchar(255),
  IN p_name varchar(255),
  IN p_class int,
  IN p_unlock_level int,
  IN p_pet_json longtext
)
BEGIN
  DECLARE v_wear text;

  IF p_user IS NULL OR p_user NOT REGEXP '^[A-Za-z0-9]{4,15}$' THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Tai khoan phai gom 4-15 ky tu chu/so';
  END IF;
  IF p_pass IS NULL OR CHAR_LENGTH(p_pass) < 4 OR CHAR_LENGTH(p_pass) > 32 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Mat khau phai gom 4-32 ky tu';
  END IF;
  IF p_name IS NULL OR p_name NOT REGEXP '^[A-Za-z0-9]{4,15}$' THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Ten nhan vat phai gom 4-15 ky tu chu/so';
  END IF;
  IF p_class NOT BETWEEN 0 AND 3 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'He phai chi nhan gia tri 0, 1, 2 hoac 3';
  END IF;
  IF p_unlock_level NOT BETWEEN 1 AND 200 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Level mo khoa pet phai tu 1 den 200';
  END IF;
  IF JSON_VALID(p_pet_json) = 0 OR JSON_LENGTH(p_pet_json) <> 12 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Du lieu full pet khong hop le';
  END IF;
  IF EXISTS (SELECT 1 FROM `account` WHERE `user` = p_user) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Tai khoan da ton tai';
  END IF;
  IF EXISTS (SELECT 1 FROM `player` WHERE `name` = p_name) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Ten nhan vat da ton tai';
  END IF;

  SET v_wear = CASE p_class
    WHEN 0 THEN '[[0,0,8,1,0,0,0,0,[[0,54],[40,120]],0],[80,0,0,1,16,0,0,0,[[14,52],[16,100]],1],[120,0,1,1,24,0,0,0,[[14,18],[25,3]],7]]'
    WHEN 1 THEN '[[5,1,9,1,1,0,0,0,[[0,54],[40,120]],0],[105,1,0,1,21,0,1,0,[[14,52],[20,100]],1],[145,1,1,1,29,0,1,0,[[14,18],[24,3]],7]]'
    WHEN 2 THEN '[[10,2,11,1,2,0,0,0,[[0,50],[40,120]],0],[90,2,0,1,18,0,2,0,[[14,42],[16,200]],1],[50,2,2,1,10,0,2,0,[[7,200],[14,12]],6],[130,2,1,1,26,0,2,0,[[14,12],[26,4]],7]]'
    WHEN 3 THEN '[[15,3,10,1,3,0,0,0,[[0,50],[40,120]],0],[95,3,0,1,19,0,3,0,[[14,44],[16,200]],1],[55,3,2,1,11,0,3,0,[[7,200],[14,14]],6],[135,3,1,1,27,0,3,0,[[14,14],[24,4]],7]]'
  END;

  START TRANSACTION;

  INSERT INTO `account`
    (`user`, `pass`, `char`, `status`, `lock`, `coin`, `phone`, `ip`, `tiennap`, `pass2`, `tongnap`)
  VALUES
    (p_user, p_pass, JSON_ARRAY(p_name), 1, 0, 0, '0', '0', 0, '0', 0);

  INSERT INTO `player_full_pet_reward`
    (`player_name`, `unlock_level`, `pet_json`, `enabled`)
  VALUES
    (p_name, p_unlock_level, p_pet_json, 1)
  ON DUPLICATE KEY UPDATE
    `unlock_level` = VALUES(`unlock_level`),
    `pet_json` = VALUES(`pet_json`),
    `enabled` = 1;

  INSERT INTO `player` (
    `name`, `body`, `clazz`, `level`, `exp`, `site`, `item4`, `item7`, `vang`, `kimcuong`,
    `tiemnang`, `kynang`, `point1`, `point2`, `point3`, `point4`, `skill`, `item3`, `item5`,
    `itemwear`, `giftcode`, `pet`, `maxbag`, `itembox3`, `itembox4`, `itembox7`, `rms_save`,
    `date`, `diemdanh`, `eff`, `itembox5`, `friend`, `enemies`, `typeexp`,
    `medal_create_material`, `point_active`, `tutien`, `dquest`, `tinhtu_material`, `nvtt`,
    `pointarena`, `dropnlmd`, `detu`
  ) VALUES (
    p_name, '[0,8,0]', p_class, 1, 0, '[0,132,354]', '[[11,1],[14,1]]', '[]', 3000, 3000,
    5, 1, 5, 5, 5, 5, '[1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]', '[]', '[]',
    v_wear, '[]', '[]', 42, '[]', '[]', '[]', '[[],[]]',
    DATE_FORMAT(NOW(), '%a %b %d %H:%i:%s GMT %Y'), 1, '[]', '[]', '[]', '[]', 1,
    '[295,261,318,328,341,249,285,321,329,344,284,280,316,327,344,288,280,317,327,342]',
    '[3,0]', '[0,1,1,1,1,0]', '[-1,-1,0,0,0,20]', '[453,455,126,136,459]', '[-1,0,0,0]',
    0, 1, ''
  );

  COMMIT;
END$$

DELIMITER ;

CALL `create_hso_account_full_pet`(
  @account_user,
  @account_pass,
  @player_name,
  @player_class,
  @unlock_level,
  @full_pet_json
);

DROP PROCEDURE IF EXISTS `create_hso_account_full_pet`;

-- Kiem tra ket qua sau khi tao.
SELECT `id`, `user`, `char`, `status`, `lock`
  FROM `account`
 WHERE `user` = @account_user;

SELECT p.`id`, p.`name`, p.`clazz`, p.`level`, JSON_LENGTH(p.`pet`) AS `pet_count`,
       r.`unlock_level`, r.`enabled`
  FROM `player` p
  JOIN `player_full_pet_reward` r ON r.`player_name` = p.`name`
 WHERE p.`name` = @player_name;

-- Sau khi nhan vat dat moc level, thoat ra vao lai game de nap danh sach pet.
