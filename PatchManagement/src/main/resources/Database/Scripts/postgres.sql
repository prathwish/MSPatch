CREATE TABLE public.console (
	console_id integer NOT NULL,
	console_name character varying(50) NOT NULL,
	PRIMARY KEY (console_id)
);

CREATE TABLE public.day (
	day_id integer NOT NULL,
	day_name character varying(20) NOT NULL,
	PRIMARY KEY (day_id)
);

CREATE TABLE public.session (
	shift_id integer NOT NULL,
	shift integer NOT NULL,
	shift_from character varying(20) NOT NULL,
	shift_to character varying(20) NOT NULL,
	duration integer NOT NULL,
	PRIMARY KEY (shift_id)
);


CREATE TABLE public.region (
	region_id integer NOT NULL,
	region_name character varying(50) NOT NULL,
	PRIMARY KEY (region_id)
);

CREATE TABLE public.timezone (
	timezone_id integer NOT NULL,
	timezone_code character varying(50) NOT NULL,
	PRIMARY KEY (timezone_id)
);


CREATE TABLE public.customer_master_data (
	master_id integer NOT NULL,
	duration integer,
	added_date date,
	application_version character varying(20),
	comment text,
	customer_name character varying(20),
	scheduled_end_time character varying(20),
	scheduled_start_time character varying(20),
	shift_id integer NOT NULL,
	FOREIGN KEY (session_id) REFERENCES session (shift_id)
			   ON DELETE RESTRICT
			    	ON UPDATE RESTRICT,
	site_key character varying(20),
	week integer,
	console_id integer NOT NULL,
	FOREIGN KEY (console_id) REFERENCES console (console_id)
			   ON DELETE RESTRICT
			    	ON UPDATE RESTRICT,
	day_id integer NOT NULL,
	FOREIGN KEY (day_id) REFERENCES day (day_id)
			   ON DELETE RESTRICT
			    	ON UPDATE RESTRICT,
	region_id integer NOT NULL,
	FOREIGN KEY (region_id) REFERENCES region (region_id)
			   ON DELETE RESTRICT
			    	ON UPDATE RESTRICT,
	timezone_id integer NOT NULL,
	FOREIGN KEY (timezone_id) REFERENCES timezone (timezone_id)
			   ON DELETE RESTRICT
			    	ON UPDATE RESTRICT,
	creation_time timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	is_contact_cancelled boolean NOT NULL default false,
	PRIMARY KEY (master_id)
);

CREATE TABLE public.csm_details (
	csm_id integer NOT NULL,
	additional_contacts character varying(254),
	contact_number character varying(254),
	csm_email_id character varying(500),
	csm_name character varying(254),
	site_key character varying(254),
	master_id integer,
	PRIMARY KEY (csm_id),
	FOREIGN KEY (master_id) REFERENCES customer_master_data (master_id)
			   ON DELETE RESTRICT
			    	ON UPDATE RESTRICT
);





CREATE TABLE public.customer_details (
	customer_id integer NOT NULL,
	contact_number character varying(20),
	customere_mail_id character varying(254),
	customer_name character varying(254),
	site_key character varying(20),
	master_id integer,
	PRIMARY KEY (customer_id),
	FOREIGN KEY (master_id) REFERENCES customer_master_data (master_id)
			   ON DELETE RESTRICT
			    	ON UPDATE RESTRICT
);
