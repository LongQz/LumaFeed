-- LumaFeed local demonstration data.
-- The 30 posts are distributed across 12 authors and kept inside the Agent's
-- supported 90-day analytics window.

START TRANSACTION;

UPDATE tb_user
SET nick_name = CONCAT('杭州食客', LPAD(id, 4, '0')),
    password = 'qzh';

UPDATE tb_user SET nick_name = '林小满' WHERE id = 1;
UPDATE tb_user SET nick_name = '可可同学' WHERE id = 2;
UPDATE tb_user SET nick_name = '陈一诺' WHERE id = 4;
UPDATE tb_user SET nick_name = '唐小糖' WHERE id = 5;
UPDATE tb_user SET nick_name = '周予安' WHERE id = 6;
UPDATE tb_user SET nick_name = '沈知夏' WHERE id = 10;
UPDATE tb_user SET nick_name = '顾清禾' WHERE id = 11;
UPDATE tb_user SET nick_name = '许星野' WHERE id = 12;
UPDATE tb_user SET nick_name = '苏念' WHERE id = 13;
UPDATE tb_user SET nick_name = '叶晚晴' WHERE id = 14;
UPDATE tb_user SET nick_name = '江屿' WHERE id = 15;
UPDATE tb_user SET nick_name = '程橙' WHERE id = 16;

INSERT INTO tb_blog (shop_id, user_id, title, images, content, liked, comments, create_time, update_time) VALUES
(1, 1, '杭州小巷里的宝藏茶餐厅，人均35吃到扶墙', '/imgs/blogs/blog1.jpg', '周末在运河边闲逛时发现的茶餐厅，菠萝油和冻柠茶都很稳，饭点稍微排队但翻台很快。', 126, 18, DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(2, 1, '运河边新开的铜锅涮肉，秋日聚餐太合适', '/imgs/blogs/0/5/e3e7c9e7-6654-4065-b47a-b86eb4541c33.jpg', '清汤锅底保留羊肉本味，麻酱和糖蒜搭配起来很舒服，四个人点两盘肉就够。', 98, 12, DATE_SUB(NOW(), INTERVAL 22 HOUR), DATE_SUB(NOW(), INTERVAL 22 HOUR)),
(3, 1, '新白鹿还是一如既往稳，三道必点菜分享', '/imgs/blogs/1/2/38f7e67d-7b26-41fd-bcf5-eebf71914230.jpg', '蛋黄鸡翅、糖醋排骨和粉丝煲发挥稳定，人多来吃更容易多点几道。', 174, 26, DATE_SUB(NOW(), INTERVAL 38 HOUR), DATE_SUB(NOW(), INTERVAL 38 HOUR)),
(4, 2, '乐堤港这家西餐氛围感拉满，约会可以冲', '/imgs/blogs/12/1/1afd01e5-0e16-43c2-b655-4bf733f95cfc.jpg', '餐厅灯光舒服，战斧牛排火候不错，建议提前订靠窗位置。', 142, 21, DATE_SUB(NOW(), INTERVAL 54 HOUR), DATE_SUB(NOW(), INTERVAL 54 HOUR)),
(5, 2, '海底捞隐藏吃法实测，番茄锅底真的百搭', '/imgs/blogs/14/13/2ec4622e-de82-4bb3-9acf-33b589850cd2.jpg', '牛肉粒番茄汤和虾滑面筋都值得试，服务员给的分量建议也很实用。', 233, 35, DATE_SUB(NOW(), INTERVAL 70 HOUR), DATE_SUB(NOW(), INTERVAL 70 HOUR)),
(6, 2, '老北京涮锅初体验，羊肉和麻酱都很在线', '/imgs/blogs/4/0/26a7cd7e-6320-432c-a0b4-1b7418f45ec7.jpg', '铜锅上桌很有氛围，羊肉不膻，配菜推荐冻豆腐和白菜。', 87, 9, DATE_SUB(NOW(), INTERVAL 86 HOUR), DATE_SUB(NOW(), INTERVAL 86 HOUR)),
(7, 4, '烤鱼控集合，拱墅万达这家口味很能打', '/imgs/blogs/4/7/863cc302-d150-420d-a596-b16e9232a1a6.jpg', '麻辣味香气足但不会过咸，鱼肉外焦里嫩，两个人选中份刚刚好。', 119, 16, DATE_SUB(NOW(), INTERVAL 102 HOUR), DATE_SUB(NOW(), INTERVAL 102 HOUR)),
(8, 4, '运河上街寿司小店，午市套餐性价比不错', '/imgs/blogs/2/3/39d65be4-af94-458d-b6de-bc8ca20735bb.jpg', '寿司米口感不错，茶碗蒸和味增汤也在线，午休时间来吃很方便。', 76, 8, DATE_SUB(NOW(), INTERVAL 118 HOUR), DATE_SUB(NOW(), INTERVAL 118 HOUR)),
(9, 5, '羊蝎子火锅降温必吃，汤底越煮越香', '/imgs/blogs/3/14/a6b9ec21-6708-4d6d-8f77-ed31c856e5b2.jpg', '羊肉炖得很软，先啃骨头再涮菜很适合朋友聚餐，整体略重口。', 105, 14, DATE_SUB(NOW(), INTERVAL 134 HOUR), DATE_SUB(NOW(), INTERVAL 134 HOUR)),
(10, 5, '周末KTV放松局，运河上街这家音响不错', '/imgs/blogs/11/12/8b37d208-9414-4e78-b065-9199647bb3e3.jpg', '包厢隔音和麦克风状态都不错，零食饮料价格透明，下午场更划算。', 63, 7, DATE_SUB(NOW(), INTERVAL 150 HOUR), DATE_SUB(NOW(), INTERVAL 150 HOUR)),
(11, 5, '水晶城KTV聚会体验，包厢空间比想象中大', '/imgs/blogs/15/9/cea51d9b-ac15-49f6-b9f1-9cf81e9b9c85.jpg', '十个人坐也不拥挤，点歌系统反应快，生日布置可以提前沟通。', 58, 6, DATE_SUB(NOW(), INTERVAL 166 HOUR), DATE_SUB(NOW(), INTERVAL 166 HOUR)),
(12, 6, '乐堤港夜生活打卡，朋友聚会氛围感很足', '/imgs/blogs/14/3/52b290eb-8b5d-403b-8373-ba0bb856d18e.jpg', '晚上灯光很漂亮，适合吃完饭继续聊天拍照，周末人多建议提前预约。', 91, 11, DATE_SUB(NOW(), INTERVAL 182 HOUR), DATE_SUB(NOW(), INTERVAL 182 HOUR)),
(1, 6, '杭州周末散步路线，运河边拍照很出片', '/imgs/blogs/7/14/4771fefb-1a87-4252-816c-9f7ec41ffa4a.jpg', '从运河广场一路走到历史街区，傍晚光线最好，沿途有不少咖啡店。', 188, 24, DATE_SUB(NOW(), INTERVAL 198 HOUR), DATE_SUB(NOW(), INTERVAL 198 HOUR)),
(2, 6, '本地人私藏的港式茶餐厅，下午茶也惊喜', '/imgs/blogs/2/1/7b31a5da-4ee3-4c9e-a38a-2dbc8de0345a.jpg', '下午茶套餐比单点划算，西多士和奶茶都不错，环境适合朋友聊天。', 84, 10, DATE_SUB(NOW(), INTERVAL 214 HOUR), DATE_SUB(NOW(), INTERVAL 214 HOUR)),
(3, 10, '人均50的火锅局，学生党聚会可以参考', '/imgs/blogs/5/2/fdc6cbb0-ddc5-4525-884a-9e88f07cf2d8.jpg', '荤素搭配后人均不贵，锅底选择多，晚高峰最好提前取号。', 112, 15, DATE_SUB(NOW(), INTERVAL 230 HOUR), DATE_SUB(NOW(), INTERVAL 230 HOUR)),
(4, 10, '第一次吃老北京铜锅，清汤锅也能这么香', '/imgs/blogs/6/8/5593ee62-04e1-410a-b6a7-4c1d50d2aee2.jpg', '原本担心味道淡，结果羊肉蘸麻酱特别香，最后煮面也很入味。', 79, 8, DATE_SUB(NOW(), INTERVAL 246 HOUR), DATE_SUB(NOW(), INTERVAL 246 HOUR)),
(5, 10, '新白鹿聚餐不踩雷点单攻略，四个人刚刚好', '/imgs/blogs/1/12/ab329e30-b4e1-446e-993b-b00be783f142.jpg', '四个人点五菜一汤比较合适，甜品可以最后再加，避免一开始点太多。', 167, 20, DATE_SUB(NOW(), INTERVAL 262 HOUR), DATE_SUB(NOW(), INTERVAL 262 HOUR)),
(6, 11, '乐堤港晚餐约会记录，环境和菜品都在线', '/imgs/blogs/13/1/6b091b4c-7870-4df8-9b21-5db9852db005.jpg', '前菜和主菜节奏不错，牛排建议五分熟，甜点偏甜但适合分享。', 131, 17, DATE_SUB(NOW(), INTERVAL 278 HOUR), DATE_SUB(NOW(), INTERVAL 278 HOUR)),
(7, 11, '海底捞服务细节分享，生日聚会体验不错', '/imgs/blogs/3/15/af0db3fa-5cf9-4eeb-a9fe-938de64bddc6.jpg', '提前备注生日后店里准备了小惊喜，等位时的小吃和饮品也比较丰富。', 204, 31, DATE_SUB(NOW(), INTERVAL 294 HOUR), DATE_SUB(NOW(), INTERVAL 294 HOUR)),
(8, 12, '拱墅万达烤鱼测评，麻辣和蒜香怎么选', '/imgs/blogs/4/1/fa74a6d6-3026-4cb7-b0b6-35abb1e52d11.jpg', '想吃过瘾选麻辣，想更耐吃选蒜香，配菜推荐土豆片和豆皮。', 95, 13, DATE_SUB(NOW(), INTERVAL 310 HOUR), DATE_SUB(NOW(), INTERVAL 310 HOUR)),
(9, 12, '运河上街日料午饭，寿司和茶碗蒸值得点', '/imgs/blogs/10/7/7e97f47d-eb49-4dc9-a583-95faa7aed287.jpg', '午市定食分量适中，鱼生新鲜，附近上班族来吃很合适。', 72, 7, DATE_SUB(NOW(), INTERVAL 326 HOUR), DATE_SUB(NOW(), INTERVAL 326 HOUR)),
(10, 13, '杭州降温后的第一顿羊蝎子，暖胃又满足', '/imgs/blogs/15/0/b61bf453-28fc-4824-84c8-783803464c4f.jpg', '汤底香料味比较足，羊肉软烂，最后加手工面特别合适。', 116, 14, DATE_SUB(NOW(), INTERVAL 342 HOUR), DATE_SUB(NOW(), INTERVAL 342 HOUR)),
(11, 13, 'KTV聚会怎么点歌，这份歌单气氛不冷场', '/imgs/blogs/14/14/cf2326ab-510a-457b-931a-04f5e9c61d36.png', '开场用合唱热场，中段加经典老歌，最后留几首收尾情歌，基本不会冷场。', 68, 9, DATE_SUB(NOW(), INTERVAL 358 HOUR), DATE_SUB(NOW(), INTERVAL 358 HOUR)),
(12, 14, '水晶城周末唱歌局，下午场更划算', '/imgs/blogs/12/9/aab78497-ec7a-46da-820a-0ee0bbe103a3.jpg', '下午场价格比晚上低不少，三小时足够，附近吃饭也方便。', 54, 5, DATE_SUB(NOW(), INTERVAL 374 HOUR), DATE_SUB(NOW(), INTERVAL 374 HOUR)),
(1, 14, '运河夜游随手拍，灯光和水面都很治愈', '/imgs/blogs/14/13/31bc9613-3637-4a36-9082-e37ce0909cbd.jpg', '晚上沿河散步很舒服，桥边和街区入口都比较适合拍照。', 183, 22, DATE_SUB(NOW(), INTERVAL 390 HOUR), DATE_SUB(NOW(), INTERVAL 390 HOUR)),
(2, 15, '杭州茶餐厅探店，菠萝油和冻柠茶必点', '/imgs/blogs/2/2/852f49b7-432c-4d01-b7b1-8e152854f2a9.jpg', '菠萝油外皮酥，冻柠茶酸甜平衡，叉烧饭也很适合中午来吃。', 102, 12, DATE_SUB(NOW(), INTERVAL 406 HOUR), DATE_SUB(NOW(), INTERVAL 406 HOUR)),
(3, 15, '铜锅涮肉蘸料搭配，我的私藏配方', '/imgs/blogs/0/0/feb2a83e-5646-46c0-a982-5e43fed31b22.jpg', '麻酱加腐乳和韭菜花是基础，再放一点香菜和辣椒油，香味更明显。', 147, 19, DATE_SUB(NOW(), INTERVAL 422 HOUR), DATE_SUB(NOW(), INTERVAL 422 HOUR)),
(4, 16, '新白鹿家庭聚餐记录，老人小孩都能吃', '/imgs/blogs/1/2/4a7b496b-2a08-4af7-aa95-df2c3bd0ef97.jpg', '菜整体口味不重，长辈接受度高，小孩喜欢甜口的鸡翅和玉米汁。', 128, 16, DATE_SUB(NOW(), INTERVAL 438 HOUR), DATE_SUB(NOW(), INTERVAL 438 HOUR)),
(5, 16, '乐堤港周末闲逛，顺便吃了一顿好晚餐', '/imgs/blogs/13/4/1bb3d189-a34a-4809-b062-323cda7d19a9.jpg', '商场动线舒服，逛累了直接吃饭很方便，晚市建议提前排号。', 89, 10, DATE_SUB(NOW(), INTERVAL 454 HOUR), DATE_SUB(NOW(), INTERVAL 454 HOUR)),
(6, 16, '运河上街吃喝玩乐一天，路线直接抄作业', '/imgs/blogs/3/12/9b19dd28-1eb1-4b70-bdc2-95544eecfeb2.jpg', '中午吃日料，下午沿河散步，晚上唱歌，一天安排紧凑但不会太赶。', 156, 18, DATE_SUB(NOW(), INTERVAL 470 HOUR), DATE_SUB(NOW(), INTERVAL 470 HOUR));

COMMIT;
