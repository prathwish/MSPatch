drop database if exists patchmanagement;

CREATE DATABASE patchmanagement
DEFAULT CHARACTER SET utf8
DEFAULT COLLATE utf8_general_ci;

USE patchmanagement;

source ./com.archcap.patchmanagement.schema.CODE_TABLE.sql
source ./com.archcap.patchmanagement.schema.CODE_VALUE.sql
source ./com.archcap.patchmanagement.schema.REVISION_INFO.sql
source ./com.archcap.patchmanagement.seeddata.CODE_TABLE.sql
source ./com.archcap.patchmanagement.seeddata.CODE_VALUE.sql
