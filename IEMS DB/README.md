# Database Migration Guide

This folder contains the SQL scripts needed to set up the IEMS database.

## Initial Setup

1. Create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS iems_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
   USE iems_db;
   ```

2. Import the full schema (no data):
   ```bash
   mysql -u root -p iems_db < "IEMS DB/V1__initial_schema.sql"
   ```

3. Import seed data (optional — adds default departments, positions, shifts):
   ```bash
   mysql -u root -p iems_db < "IEMS DB/V2__seed_data.sql"
   ```

## Individual Table Scripts

Each `.sql` file in this folder corresponds to a single table and can be imported independently if needed. The full schema script (`V1__initial_schema.sql`) is the recommended way to initialize.

## Versioning Convention

Migration scripts follow the naming pattern `V{number}__{description}.sql`:
- `V1__initial_schema.sql` — Full database schema (tables, views, triggers)
- `V2__seed_data.sql` — Default seed/reference data
- Future migrations: `V3__add_column_xyz.sql`, etc.

This convention is compatible with Flyway if you decide to adopt it later.
