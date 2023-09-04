INSERT INTO public.user_groups (id, "name",created_at,updated_at,deleted_at) VALUES
	(1, 'Admin','2023-08-07 20:23:10.273036','2023-08-07 20:23:10.273036',NULL),
	(2, 'Clients','2023-08-07 20:23:10.273036','2023-08-07 20:23:10.273036',NULL);
ALTER SEQUENCE user_groups_id_seq RESTART WITH 3;

INSERT INTO public.users (id, first_name,last_name,email,birth_date,cellphone,"password",user_group_id,user_created,user_modified,created_at,updated_at,deleted_at) VALUES
	(1, 'admin','','admin@admin.com','2023-07-10','','$2b$12$FqYd6qWfqNZksbp5nVwjmu3UmbP2gPc55oF2FFalnx4kgX/8tKaiK',1,NULL,NULL,'2023-08-07 20:23:10.275763','2023-08-07 20:23:10.275763',NULL);
ALTER SEQUENCE users_id_seq RESTART WITH 2;

INSERT INTO public.events (id, "name",programmed_date_initial,programmed_date_final,address,is_public,user_created,user_modified,created_at,updated_at,deleted_at) VALUES
	(1, 'Festival do Japão','2021-08-07','2024-08-07',NULL,true,1,1,'2023-08-07 16:19:08.093462','2023-08-07 16:19:08.093462',NULL),
	(2, 'Bon-odori','2021-08-07','2024-08-07',NULL,false,1,1,'2023-08-07 16:19:24.450865','2023-08-07 16:19:24.450865',NULL);
ALTER SEQUENCE events_id_seq RESTART WITH 3;

INSERT INTO public.stand_categories (id, "name",event_id,user_created,user_modified,created_at,updated_at,deleted_at) VALUES
	(1, 'Caixa',1,1,1,'2023-08-07 16:21:53.252553','2023-08-07 16:21:53.252553',NULL),
	(2, 'Caixa',2,1,1,'2023-08-07 16:21:53.257345','2023-08-07 16:21:53.257345',NULL),
	(3, 'Bebidas',1,1,1,'2023-08-07 16:32:22.196678','2023-08-07 16:32:22.196678',NULL),
	(4, 'Bebidas',2,1,1,'2023-08-07 16:32:22.199389','2023-08-07 16:32:22.199389',NULL),
	(5, 'Comidas',1,1,1,'2023-08-07 16:32:22.200865','2023-08-07 16:32:22.200865',NULL),
	(6, 'Comidas',2,1,1,'2023-08-07 16:32:22.201612','2023-08-07 16:32:22.201612',NULL),
	(7, 'Doce', 1, 1, 1, '2023-08-07 16:32:22.201', '2023-08-07 16:32:22.201', NULL),
	(8, 'Doce', 2, 1, 1, '2023-08-07 16:32:22.201', '2023-08-07 16:32:22.201', NULL);
ALTER SEQUENCE stand_categories_id_seq RESTART WITH 9;

INSERT INTO public.stands (id, "name",stand_category_id,event_id,user_created,user_modified,created_at,updated_at,deleted_at,is_cashier) VALUES
	(1,'Caixa',1,1,1,1,'2023-08-07 16:22:07.67914','2023-08-07 16:22:07.67914',NULL,true),
	(2,'Caixa',2,2,1,1,'2023-08-07 16:22:07.682556','2023-08-07 16:22:07.682556',NULL,true),
	(3,'Bebidas',3,1,1,1,'2023-08-07 16:32:51.646146','2023-08-07 16:32:51.646146',NULL,false),
	(4,'Bebidas',4,2,1,1,'2023-08-07 16:32:51.647484','2023-08-07 16:32:51.647484',NULL,false),
	(5,'Sobá',5,1,1,1,'2023-08-07 16:32:51.647','2023-08-07 16:32:51.647',NULL,false),
	(6,'Yakisoba',5,1,1,1,'2023-08-07 16:32:51.647','2023-08-07 16:32:51.647',NULL,false),
	(7,'Espetinho',5,1,1,1,'2023-08-07 16:32:51.647','2023-08-07 16:32:51.647',NULL,false),
	(8,'Lamen',5,1,1,1,'2023-08-07 16:32:51.647','2023-08-07 16:32:51.647',NULL,false),
	(9,'Sobá',6,2,1,1,'2023-08-07 16:34:23.678684','2023-08-07 16:34:23.678684',NULL,false),
	(10,'Kare',6,2,1,1,'2023-08-07 16:34:23.679768','2023-08-07 16:34:23.679768',NULL,false),
	(11, 'Doce', 7, 1, 1, 1, '2023-08-09 22:50:45.396', '2023-08-09 22:50:45.396', NULL, false);
ALTER SEQUENCE stands_id_seq RESTART WITH 12;

INSERT INTO public.user_event_stands (user_id,event_id,stand_id,is_responsible,user_created,user_modified,created_at,updated_at,deleted_at) VALUES
	(1,1,1,true,1,1,'2023-08-07 16:23:13.384604','2023-08-07 16:23:13.384604',NULL),
	(1,2,2,true,1,1,'2023-08-07 16:23:21.151784','2023-08-07 16:23:21.151784',NULL),
	(1,1,3,true,1,1,'2023-08-07 16:35:14.06849','2023-08-07 16:35:14.06849',NULL),
	(1,2,4,true,1,1,'2023-08-07 16:35:14.07113','2023-08-07 16:35:14.07113',NULL),
	(1,1,5,true,1,1,'2023-08-07 16:35:22.25245','2023-08-07 16:35:22.25245',NULL),
	(1,1,6,true,1,1,'2023-08-07 16:35:31.344648','2023-08-07 16:35:31.344648',NULL),
	(1,1,7,true,1,1,'2023-08-07 16:35:41.128274','2023-08-07 16:35:41.128274',NULL),
	(1,1,8,true,1,1,'2023-08-07 16:35:50.519213','2023-08-07 16:35:50.519213',NULL),
	(1,2,9,true,1,1,'2023-08-07 16:36:48.637657','2023-08-07 16:36:48.637657',NULL),
	(1,2,10,true,1,1,'2023-08-07 16:36:48.638935','2023-08-07 16:36:48.638935',NULL);

INSERT INTO public.product_categories (id, "name", user_created, user_modified, created_at, updated_at, deleted_at, event_id) VALUES
	(1, 'Comida', 1, 1, '2023-08-09 22:32:34.794', '2023-08-09 22:32:34.794', NULL, 1),
	(2, 'Bebida', 1, 1, '2023-08-09 22:32:34.800', '2023-08-09 22:32:34.800', NULL, 1),
	(3, 'Doce', 1, 1, '2023-08-09 22:32:34.804', '2023-08-09 22:32:34.804', NULL, 1);
ALTER SEQUENCE product_categories_id_seq RESTART WITH 4;

INSERT INTO public.products (id, "name", price, product_category_id, user_created, user_modified, created_at, updated_at, deleted_at, custom_form_template, stand_id) VALUES
	(1, 'Sobá', 30.0, 1, 1, 1, '2023-08-09 22:37:37.648', '2023-08-09 22:37:37.648', NULL, NULL, 5),
	(2, 'Espetinho', 15.0, 1, 1, 1, '2023-08-09 22:37:37.654', '2023-08-09 22:37:37.654', NULL, NULL, 7),
	(3, 'Yakisoba', 40.0, 1, 1, NULL, '2023-08-09 22:37:37.658', '2023-08-09 22:37:37.658', NULL, NULL, 6),
	(6, 'Suco', 5.0, 2, 1, 1, '2023-08-09 22:37:37.663', '2023-08-09 22:37:37.663', NULL, NULL, 3),
	(4, 'Coca cola', 5.0, 2, 1, 1, '2023-08-09 22:37:37.667', '2023-08-09 22:37:37.667', NULL, NULL, 3),
	(5, 'Água', 3.0, 2, 1, 1, '2023-08-09 22:37:37.671', '2023-08-09 22:37:37.671', NULL, NULL, 3),
	(7, 'Algodão doce', 4.0, 3, 1, 1, '2023-08-09 22:37:37.675', '2023-08-09 22:37:37.675', NULL, NULL, 11),
	(8, 'Brigadeiro', 2.0, 3, 1, 1, '2023-08-09 22:37:37.679', '2023-08-09 22:37:37.679', NULL, NULL, 11),
	(9, 'Sorvete frito', 15.0, 3, 1, 1, '2023-08-09 22:37:37.684', '2023-08-09 22:37:37.684', NULL, NULL, 11);
ALTER SEQUENCE products_id_seq RESTART WITH 10;
