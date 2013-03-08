# db-migrator

db-migrator is an Apache Ant based database versioning tool influenced by Ruby on Rails Migrations project.
<br/>
To use:
<br/>
1. Download the binary version of db-migrator
2. Create a directory near your project called "database"
3. Add your jdbc driver for your database to the database directory
4. Update migrator.properties to specify the jdbc parameters to connect to your database
5. Create migration scripts
<br/>
## Migrations Directory
<br/>
Migration scripts are named so that the database 'version' is specified in the first few characters.  It doesn't matter what the format of this prefix is, so long as the filename starts with a number.  The versions of the database increment or decrement according to the numbers specified in the list of filenames in the migrations directory, for example:
<br/>
* 001-create_tables.sql
* 002-create_users.sql
* 005-add_password_field.sql
<br/>
The database versions would be 1, 2, and 5 respectively.

## Migration Scripts

The migration scripts contain two sections: 
<br/>
1. @UP@ which defines the standard sql to upgrade the database to this version
2. @DOWN@ which defines to rollback sql to downgrade to the previous version
<br/>
For example:

<pre>
@UP@
create table meeting_reminder (
        id serial primary key,
        event_id integer not null,              -- the meeting this reminder is for
        amount integer not null,                -- how many days/hours/weeks before to remind
        calendar_unit varchar(10) not null,     -- what the amount is: days/weeks/hours
        is_sent boolean not null,               -- true indicates this reminder has been sent
        version INTEGER NOT NULL                                 

);
alter table meeting_reminder add constraint fk_meeting_reminder_event foreign key (event_id) references event (id);
@UP

@DOWN
drop table meeting_reminder;
@DOWN
</pre>
## Usage
<br/>
To run db-migrator, change into the database directory where migrator.xml is located and run
<br/>
<code>
ant -f migrator.xml init  
</code>
<br/>
This will create a table in the database called db_version, which contains one column called version and will only ever contain a single row, initialized to 0.
<br/>
<code>
ant -f migrator.xml upgrade
</code>
<br/>
Will read the version from the db-version table and search the migrations directory for the next highest number and run the sql in the @UP@.  It will then increment the db-version.version to the number specified in the migration script name.  If the upgrade failed, the version will remain the same (see H2 databse note below).
<br/>
<code>
ant -f migrator.xml rollback
</code>
<br/>
will read the version from the db-version table and search the migrations directory for the same number and run the sql in the @DOWN@.  If will then decrement the db-version.version to the number specified in the next lowest migration script name.  If the downgrade failed, the version will remain the same (see H2 database note below).
<br/>
<code>
ant -f migrator.xml upgrade-all
</code>
<br/>
Is a shortcut for running all scripts from the current version to the highest number specified in the migrations directory.
<br/>
If things go wrong, you can always run the script sql manually and manually update the db-version.version value to the appropriate number.  The system is designed to be non-intelligent in this regard.
<br/>
H2 database note:  from my experience using this tool with H2, H2 will not enforce transactions around schema changes (DDL) - it auto-commits the alter table or create table regardless of a transaction that may be rolled back later on.  I've just had to manually drop tables when I mess up an @UP@ script using H2.
<br/>
Have fun!