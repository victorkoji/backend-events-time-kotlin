--liquibase formatted sql

--changeset victor.koji:202308282104-1 endDelimiter://
--comment: Create all tables to project
--
--CREATE TABLE public.roles (
--	id serial4 NOT NULL,
--	"name" varchar(20) NOT NULL,
--  CONSTRAINT roles_pkey PRIMARY KEY (id)
--);

CREATE TABLE public.payment_methods (
	id serial4 NOT NULL,
	"name" varchar(50) NOT NULL,
	CONSTRAINT payment_methods_pkey PRIMARY KEY (id)
);

CREATE TABLE public.status_charges (
	id serial4 NOT NULL,
	"name" varchar(255) NOT NULL,
	CONSTRAINT status_charges_pkey PRIMARY KEY (id)
);

CREATE TABLE public.status_stand_orders (
	id serial4 NOT NULL,
	"name" varchar(50) NOT NULL,
	CONSTRAINT status_stand_orders_pkey PRIMARY KEY (id)
);

CREATE TABLE public.status_vouchers (
	id serial4 NOT NULL,
	"name" varchar(50) NOT NULL,
	CONSTRAINT status_vouchers_pkey PRIMARY KEY (id)
);

CREATE TABLE public.user_groups (
	id serial4 NOT NULL,
	"name" varchar(255) NOT NULL,
	created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	deleted_at timestamp(6) NULL,
	CONSTRAINT user_groups_pkey PRIMARY KEY (id)
);

CREATE TABLE public.users (
	id serial4 NOT NULL,
	first_name varchar(255) NOT NULL,
	last_name varchar(255) NULL,
	email varchar(255) NULL,
	birth_date date NULL,
	cellphone varchar(255) NULL,
	"password" varchar(255) NOT NULL,
	user_group_id int4 NULL,
	user_created int4 NULL,
	user_modified int4 NULL,
	created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	deleted_at timestamp(6) NULL,
	CONSTRAINT users_pkey PRIMARY KEY (id),
	CONSTRAINT users_user_created_foreign FOREIGN KEY (user_created) REFERENCES public.users(id),
	CONSTRAINT users_user_group_id_foreign FOREIGN KEY (user_group_id) REFERENCES public.user_groups(id),
	CONSTRAINT users_user_modified_foreign FOREIGN KEY (user_modified) REFERENCES public.users(id)
);

--CREATE TABLE public.user_roles (
--	id serial4 NOT NULL,
--	user_id int4 NOT NULL,
--	role_id int4 NOT NULL,
--	CONSTRAINT user_roles_pkey PRIMARY KEY (id),
--  CONSTRAINT user_roles_user_id_foreign FOREIGN KEY (user_id) REFERENCES public.users(id),
--  CONSTRAINT user_roles_role_id_foreign FOREIGN KEY (role_id) REFERENCES public.roles(id)
--);

CREATE TABLE public.user_tokens (
	id serial4 NOT NULL,
	app_client varchar(50) NOT NULL,
	user_id int4 NOT NULL,
	token_fcm varchar(255) NULL,
	refresh_token varchar(255) NULL,
	CONSTRAINT user_tokens_pkey PRIMARY KEY (id),
	CONSTRAINT user_tokens_user_id_foreign FOREIGN KEY (user_id) REFERENCES public.users(id)
);

CREATE TABLE public.events (
	id serial4 NOT NULL,
	"name" varchar(255) NOT NULL,
	programmed_date_initial date NULL,
	programmed_date_final date NULL,
	address varchar(255) NULL,
	is_public bool NOT NULL DEFAULT false,
	user_created int4 NULL,
	user_modified int4 NULL,
	created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	deleted_at timestamp(6) NULL,
	CONSTRAINT events_pkey PRIMARY KEY (id),
	CONSTRAINT events_user_created_foreign FOREIGN KEY (user_created) REFERENCES public.users(id),
	CONSTRAINT events_user_modified_foreign FOREIGN KEY (user_modified) REFERENCES public.users(id)
);

CREATE TABLE public.product_categories (
	id serial4 NOT NULL,
	"name" varchar(255) NOT NULL,
	event_id int4 NOT NULL,
	user_created int4 NULL,
	user_modified int4 NULL,
	created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	deleted_at timestamp(6) NULL,
	CONSTRAINT product_categories_pkey PRIMARY KEY (id),
	CONSTRAINT product_categories_event_id_foreign FOREIGN KEY (event_id) REFERENCES public.events(id),
	CONSTRAINT product_categories_user_created_foreign FOREIGN KEY (user_created) REFERENCES public.users(id),
	CONSTRAINT product_categories_user_modified_foreign FOREIGN KEY (user_modified) REFERENCES public.users(id)
);

CREATE TABLE public.product_files (
	id serial4 NOT NULL,
	filename varchar(255) NOT NULL,
	filename_original varchar(255) NOT NULL,
	media_type varchar(100) NOT NULL,
	filepath varchar(255) NOT NULL,
	user_created int4 NULL,
	user_modified int4 NULL,
	created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	deleted_at timestamp(6) NULL,
	CONSTRAINT product_files_pkey PRIMARY KEY (id),
	CONSTRAINT product_files_user_created_foreign FOREIGN KEY (user_created) REFERENCES public.users(id),
	CONSTRAINT product_files_user_modified_foreign FOREIGN KEY (user_modified) REFERENCES public.users(id)
);

CREATE TABLE public.stand_categories (
	id serial4 NOT NULL,
	"name" varchar(255) NOT NULL,
	event_id int4 NOT NULL,
	user_created int4 NULL,
	user_modified int4 NULL,
	created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	deleted_at timestamp(6) NULL,
	CONSTRAINT stand_categories_pkey PRIMARY KEY (id),
	CONSTRAINT stand_categories_event_id_foreign FOREIGN KEY (event_id) REFERENCES public.events(id),
	CONSTRAINT stand_categories_user_created_foreign FOREIGN KEY (user_created) REFERENCES public.users(id),
	CONSTRAINT stand_categories_user_modified_foreign FOREIGN KEY (user_modified) REFERENCES public.users(id)
);

CREATE TABLE public.stands (
	id serial4 NOT NULL,
	"name" varchar(255) NOT NULL,
	is_cashier bool NOT NULL DEFAULT false,
	stand_category_id int4 NOT NULL,
	event_id int4 NOT NULL,
	user_created int4 NULL,
	user_modified int4 NULL,
	created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	deleted_at timestamp(6) NULL,
	CONSTRAINT stands_pkey PRIMARY KEY (id),
	CONSTRAINT stands_event_id_foreign FOREIGN KEY (event_id) REFERENCES public.events(id),
	CONSTRAINT stands_stand_category_id_foreign FOREIGN KEY (stand_category_id) REFERENCES public.stand_categories(id),
	CONSTRAINT stands_user_created_foreign FOREIGN KEY (user_created) REFERENCES public.users(id),
	CONSTRAINT stands_user_modified_foreign FOREIGN KEY (user_modified) REFERENCES public.users(id)
);

CREATE TABLE public.user_event_stands (
	user_id int4 NOT NULL,
	event_id int4 NOT NULL,
	stand_id int4 NOT NULL,
	is_responsible bool NOT NULL,
	user_created int4 NULL,
	user_modified int4 NULL,
	created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	deleted_at timestamp(6) NULL,
	CONSTRAINT user_event_stands_pkey PRIMARY KEY (user_id, event_id, stand_id),
	CONSTRAINT user_event_stands_event_id_foreign FOREIGN KEY (event_id) REFERENCES public.events(id),
	CONSTRAINT user_event_stands_stand_id_foreign FOREIGN KEY (stand_id) REFERENCES public.stands(id),
	CONSTRAINT user_event_stands_user_created_foreign FOREIGN KEY (user_created) REFERENCES public.users(id),
	CONSTRAINT user_event_stands_user_id_foreign FOREIGN KEY (user_id) REFERENCES public.users(id),
	CONSTRAINT user_event_stands_user_modified_foreign FOREIGN KEY (user_modified) REFERENCES public.users(id)
);


CREATE TABLE public.buyouts (
	id serial4 NOT NULL,
	total_amount float8 NOT NULL,
	approved bool NOT NULL,
	event_id int4 NULL,
	user_owner int4 NULL,
	user_created int4 NULL,
	created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	deleted_at timestamp(6) NULL,
	CONSTRAINT buyouts_pkey PRIMARY KEY (id),
	CONSTRAINT buyouts_event_id_foreign FOREIGN KEY (event_id) REFERENCES public.events(id),
	CONSTRAINT buyouts_user_created_foreign FOREIGN KEY (user_created) REFERENCES public.users(id),
	CONSTRAINT buyouts_user_owner_foreign FOREIGN KEY (user_owner) REFERENCES public.users(id)
);

CREATE TABLE public.charges (
	id serial4 NOT NULL,
	buyout_id int4 NULL,
	payment_method_id int4 NULL,
	status_charge_id int4 NULL,
	user_created int4 NULL,
	created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	deleted_at timestamp(6) NULL,
	CONSTRAINT charges_pkey PRIMARY KEY (id),
	CONSTRAINT charges_buyout_id_foreign FOREIGN KEY (buyout_id) REFERENCES public.buyouts(id),
	CONSTRAINT charges_payment_method_id_foreign FOREIGN KEY (payment_method_id) REFERENCES public.payment_methods(id),
	CONSTRAINT charges_status_charge_id_foreign FOREIGN KEY (status_charge_id) REFERENCES public.status_charges(id),
	CONSTRAINT charges_user_created_foreign FOREIGN KEY (user_created) REFERENCES public.users(id)
);

CREATE TABLE public.products (
	id serial4 NOT NULL,
	"name" varchar(255) NOT NULL,
	price float8 NOT NULL,
    custom_form_template jsonb NULL,
    stand_id int4 NOT NULL,
    product_file_id int4 NULL,
	product_category_id int4 NOT NULL,
	user_created int4 NULL,
	user_modified int4 NULL,
	created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	deleted_at timestamp(6) NULL,
	CONSTRAINT products_pkey PRIMARY KEY (id),
	CONSTRAINT products_product_category_id_foreign FOREIGN KEY (product_category_id) REFERENCES public.product_categories(id),
	CONSTRAINT products_product_file_id_foreign FOREIGN KEY (product_file_id) REFERENCES public.product_files(id),
	CONSTRAINT products_stand_id_foreign FOREIGN KEY (stand_id) REFERENCES public.stands(id),
	CONSTRAINT products_user_created_foreign FOREIGN KEY (user_created) REFERENCES public.users(id),
	CONSTRAINT products_user_modified_foreign FOREIGN KEY (user_modified) REFERENCES public.users(id)
);

CREATE TABLE public.stand_orders (
	id serial4 NOT NULL,
	total_amount float8 NOT NULL,
	num_order int4 NOT NULL,
	stand_id int4 NULL,
	user_created int4 NULL,
	created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	deleted_at timestamp(6) NULL,
	CONSTRAINT stand_orders_pkey PRIMARY KEY (id),
	CONSTRAINT stand_orders_stand_id_foreign FOREIGN KEY (stand_id) REFERENCES public.stands(id),
	CONSTRAINT stand_orders_user_created_foreign FOREIGN KEY (user_created) REFERENCES public.users(id)
);

CREATE TABLE public.menu (
	id serial4 NOT NULL,
	stand_id int4 NOT NULL,
	product_id int4 NOT NULL,
	created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	deleted_at timestamp(6) NULL,
	CONSTRAINT menu_pkey PRIMARY KEY (id),
	CONSTRAINT menu_product_id_foreign FOREIGN KEY (product_id) REFERENCES public.products(id),
	CONSTRAINT menu_stand_id_foreign FOREIGN KEY (stand_id) REFERENCES public.stands(id)
);

CREATE TABLE public.stand_order_vouchers (
	id serial4 NOT NULL,
	stand_order_id int4 NULL,
	custom_response jsonb NULL,
	created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	deleted_at timestamp(6) NULL,
	CONSTRAINT stand_order_vouchers_pkey PRIMARY KEY (id),
	CONSTRAINT stand_order_vouchers_stand_order_id_foreign FOREIGN KEY (stand_order_id) REFERENCES public.stand_orders(id)
);

CREATE TABLE public.vouchers (
	id serial4 NOT NULL,
	price float8 NOT NULL,
	buyout_id int4 NULL,
	product_id int4 NULL,
	stand_order_voucher_id int4 NULL,
	status_voucher_id int4 NULL,
	user_created int4 NULL,
	created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	deleted_at timestamp(6) NULL,
	CONSTRAINT vouchers_pkey PRIMARY KEY (id),
	CONSTRAINT vouchers_buyout_id_foreign FOREIGN KEY (buyout_id) REFERENCES public.buyouts(id),
	CONSTRAINT vouchers_product_id_foreign FOREIGN KEY (product_id) REFERENCES public.products(id),
	CONSTRAINT vouchers_stand_order_voucher_id_foreign FOREIGN KEY (stand_order_voucher_id) REFERENCES public.stand_order_vouchers(id),
	CONSTRAINT vouchers_status_voucher_id_foreign FOREIGN KEY (status_voucher_id) REFERENCES public.status_vouchers(id),
	CONSTRAINT vouchers_user_created_foreign FOREIGN KEY (user_created) REFERENCES public.users(id)
);
