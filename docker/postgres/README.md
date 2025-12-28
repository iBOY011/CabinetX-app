# PostgreSQL Database Migration - Deployment Guide

This guide provides a complete pipeline for migrating CabinetX microservices from H2/MySQL to PostgreSQL.

## 📋 Overview

- **14 microservices** now use PostgreSQL (one database per service)
- **Infrastructure**: Dockerized PostgreSQL on a dedicated DB server
- **Automated provisioning**: All databases and users created automatically
- **Security**: One role + one database per service (least-privilege principle)

## 🗂️ Services Using PostgreSQL

1. analytics-service → `analytics_db`
2. appointment-service → `appointment_db`
3. billing-service → `billing_db`
4. chatbot-service → `chatbot_db`
5. clinic-service → `clinic_db`
6. consultation-service → `consultation_db`
7. medical-record-service → `medical_record_db`
8. medication-service → `medication_db`
9. notification-service → `notification_db`
10. patient-service → `patient_db`
11. payment-service → `payment_db`
12. prescription-service → `prescription_db`
13. queue-service → `queue_db`
14. user-service → `user_db`

**Excluded services** (no database needed):
- configuration-service (Spring Cloud Config Server)
- discovery-service (Eureka Server)
- gateway-service (API Gateway)

## 🚀 Deployment Pipeline

### Step 1: Prepare DB Server

1. **SSH into your DB server**:
   ```bash
   ssh user@your-db-server-ip
   ```

2. **Install Docker & Docker Compose** (if not already installed):
   ```bash
   # Ubuntu/Debian
   sudo apt update
   sudo apt install -y docker.io docker-compose-plugin
   sudo systemctl enable --now docker
   sudo usermod -aG docker $USER
   # Log out and back in for group changes
   
   # CentOS/RHEL
   sudo yum install -y docker docker-compose-plugin
   sudo systemctl enable --now docker
   sudo usermod -aG docker $USER
   ```

3. **Create deployment directory**:
   ```bash
   mkdir -p ~/cabinetx-postgres
   cd ~/cabinetx-postgres
   ```

4. **Copy files from this repo to the server**:
   ```bash
   # From your local machine (in the repo root):
   scp -r docker/postgres/* user@your-db-server-ip:~/cabinetx-postgres/
   ```

### Step 2: Configure Passwords

1. **On the DB server**, copy the environment template:
   ```bash
   cd ~/cabinetx-postgres
   cp .env.example .env
   ```

2. **Edit `.env` and set STRONG passwords**:
   ```bash
   nano .env  # or vim .env
   ```
   
   Generate strong passwords:
   ```bash
   # Generate 14 unique passwords
   for i in {1..15}; do openssl rand -base64 32; done
   ```

3. **Secure the `.env` file**:
   ```bash
   chmod 600 .env
   ```

### Step 3: Deploy PostgreSQL

1. **Start the database**:
   ```bash
   cd ~/cabinetx-postgres
   docker compose up -d
   ```

2. **Verify deployment**:
   ```bash
   # Check container is running
   docker ps | grep cabinetx-postgres
   
   # Check logs
   docker compose logs -f postgres
   
   # Verify databases were created
   docker exec -it cabinetx-postgres psql -U postgres_admin -c "\l"
   ```

   You should see all 14 databases listed.

3. **Test a database connection**:
   ```bash
   docker exec -it cabinetx-postgres psql -U chatbot_user -d chatbot_db -c "SELECT version();"
   ```

### Step 4: Configure Firewall (CRITICAL)

**⚠️ DO NOT skip this step!** Without proper firewall rules, your database will be exposed to the internet.

#### Option A: UFW (Ubuntu/Debian)

```bash
# Enable UFW if not already enabled
sudo ufw enable

# Allow SSH (important - don't lock yourself out!)
sudo ufw allow 22/tcp

# Allow PostgreSQL ONLY from your application servers
sudo ufw allow from <APP_SERVER_IP_1> to any port 5432 proto tcp
sudo ufw allow from <APP_SERVER_IP_2> to any port 5432 proto tcp
# ... repeat for all app servers

# Deny all other access to PostgreSQL
sudo ufw deny 5432/tcp

# Check status
sudo ufw status numbered
```

#### Option B: firewalld (CentOS/RHEL)

```bash
# Enable firewalld
sudo systemctl enable --now firewalld

# Allow PostgreSQL from specific IPs
sudo firewall-cmd --permanent --add-rich-rule='rule family="ipv4" source address="<APP_SERVER_IP_1>" port port="5432" protocol="tcp" accept'
sudo firewall-cmd --permanent --add-rich-rule='rule family="ipv4" source address="<APP_SERVER_IP_2>" port port="5432" protocol="tcp" accept'
# ... repeat for all app servers

# Reload firewall
sudo firewall-cmd --reload

# Verify rules
sudo firewall-cmd --list-all
```

#### Option C: Cloud Provider Security Groups

If using AWS/Azure/GCP, configure security groups:
- **Inbound rule**: TCP port 5432
- **Source**: Only the security group or IP addresses of your application servers
- **Deny all other traffic**

### Step 5: Update Application Servers

For **each application server** running microservices:

1. **Set the database host environment variable**:
   ```bash
   export SPRING_DATASOURCE_URL=jdbc:postgresql://<DB_SERVER_IP>:5432/<service>_db
   # Example for chatbot-service:
   # export SPRING_DATASOURCE_URL=jdbc:postgresql://192.168.1.100:5432/chatbot_db
   ```

2. **Set credentials** (use the passwords from `.env` on DB server):
   ```bash
   export SPRING_DATASOURCE_USERNAME=<service>_user
   export SPRING_DATASOURCE_PASSWORD=<password_from_env_file>
   ```

3. **Run services with the `local` profile**:
   ```bash
   cd /path/to/CabinetX-app/chatbot-service
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```

   Or for production deployments, pass environment variables to your container/systemd service.

### Step 6: Production Deployment (Optional)

For production, you should:

1. **Use a secrets manager** instead of `.env` files:
   - AWS Secrets Manager
   - HashiCorp Vault
   - Azure Key Vault

2. **Change `ddl-auto` to `validate` or `none`**:
   - Edit each service's `application-local.yml`
   - Change `spring.jpa.hibernate.ddl-auto: validate`

3. **Use Flyway or Liquibase** for schema migrations:
   ```xml
   <!-- Add to each service's pom.xml -->
   <dependency>
       <groupId>org.flywaydb</groupId>
       <artifactId>flyway-core</artifactId>
   </dependency>
   ```

4. **Enable PostgreSQL backups**:
   ```bash
   # Daily backup script (on DB server)
   cat > ~/backup-postgres.sh <<'EOF'
   #!/bin/bash
   DATE=$(date +%Y%m%d_%H%M%S)
   BACKUP_DIR=~/postgres-backups
   mkdir -p $BACKUP_DIR
   docker exec cabinetx-postgres pg_dumpall -U postgres_admin | gzip > $BACKUP_DIR/backup_$DATE.sql.gz
   # Keep only last 7 days
   find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +7 -delete
   EOF
   chmod +x ~/backup-postgres.sh
   
   # Add to crontab (daily at 2 AM)
   (crontab -l 2>/dev/null; echo "0 2 * * * ~/backup-postgres.sh") | crontab -
   ```

5. **Set up monitoring**:
   - Enable PostgreSQL metrics in Actuator
   - Use Prometheus + Grafana
   - Monitor connection pool, query performance

## 🧪 Testing the Migration

### Local Testing

1. **Start PostgreSQL locally**:
   ```bash
   cd docker/postgres
   docker compose up -d
   ```

2. **Run a service locally**:
   ```bash
   cd chatbot-service
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```

3. **Verify database connection**:
   - Check application logs for connection success
   - Access actuator health endpoint: `http://localhost:8080/actuator/health`
   - Verify tables were created: 
     ```bash
     docker exec -it cabinetx-postgres psql -U chatbot_user -d chatbot_db -c "\dt"
     ```

### Unit Tests

All services now use **H2 in-memory database for tests** (no PostgreSQL needed for CI):

```bash
cd <any-service>
./mvnw test
```

Tests will automatically use H2 because the dependency scope is `test`.

## 📊 Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   Application Servers                    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Service A  │  │   Service B  │  │   Service N  │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  │
└─────────┼──────────────────┼──────────────────┼──────────┘
          │                  │                  │
          │    Firewall-protected connection   │
          └──────────────────┼──────────────────┘
                             ▼
              ┌──────────────────────────────┐
              │       DB Server (Docker)     │
              │  ┌────────────────────────┐  │
              │  │  PostgreSQL Container  │  │
              │  │  ┌──────────────────┐  │  │
              │  │  │  analytics_db    │  │  │
              │  │  │  chatbot_db      │  │  │
              │  │  │  patient_db      │  │  │
              │  │  │  ...             │  │  │
              │  │  └──────────────────┘  │  │
              │  └────────────────────────┘  │
              │  Volume: pgdata             │
              └──────────────────────────────┘
```

## 🔧 Troubleshooting

### Connection refused

**Problem**: Services can't connect to PostgreSQL.

**Solutions**:
1. Check PostgreSQL is running: `docker ps`
2. Verify firewall allows connection from app server
3. Test connection manually:
   ```bash
   psql -h <DB_SERVER_IP> -U chatbot_user -d chatbot_db
   ```
4. Check service environment variables are set correctly

### Password authentication failed

**Problem**: Wrong credentials.

**Solutions**:
1. Verify `.env` file on DB server has correct passwords
2. Ensure app server environment variables match
3. Check for typos in database/user names

### Databases not created

**Problem**: Init script didn't run.

**Solutions**:
1. Check logs: `docker compose logs postgres`
2. Verify init script has execute permissions:
   ```bash
   chmod +x initdb/001-create-databases.sh
   ```
3. If script didn't run on first boot:
   ```bash
   docker compose down -v  # WARNING: deletes all data
   docker compose up -d
   ```

### Schema/table errors

**Problem**: Hibernate can't create tables.

**Solutions**:
1. Verify `ddl-auto` is set to `update` (not `validate` or `none`)
2. Check PostgreSQL dialect is configured correctly
3. Review service logs for SQL errors

## 📝 Maintenance

### Adding a new service

1. Add database to init script:
   ```bash
   # Edit docker/postgres/initdb/001-create-databases.sh
   # Add service name to the 'services' array
   ```

2. Add password to `.env.example` and your actual `.env`

3. Create `application-local.yml` for the new service

4. Update this README

### Changing passwords

1. Update `.env` file on DB server
2. Run password change command:
   ```bash
   docker exec -it cabinetx-postgres psql -U postgres_admin -c "ALTER ROLE chatbot_user WITH PASSWORD 'new_password';"
   ```
3. Update application server environment variables
4. Restart services

### Database backups

See "Production Deployment" section above for automated backup setup.

**Manual backup**:
```bash
docker exec cabinetx-postgres pg_dump -U postgres_admin chatbot_db | gzip > chatbot_db_backup.sql.gz
```

**Restore**:
```bash
gunzip -c chatbot_db_backup.sql.gz | docker exec -i cabinetx-postgres psql -U postgres_admin -d chatbot_db
```

## 🔒 Security Checklist

- [ ] Strong passwords generated and stored securely
- [ ] `.env` file has `600` permissions (not committed to git)
- [ ] Firewall configured to allow only app servers
- [ ] PostgreSQL not exposed to public internet
- [ ] Daily backups configured
- [ ] Backup retention policy implemented
- [ ] Schema migrations use Flyway/Liquibase (production)
- [ ] `ddl-auto` set to `validate` or `none` (production)
- [ ] Connection pooling configured (HikariCP)
- [ ] SSL/TLS enabled for connections (optional but recommended)

## 📚 Additional Resources

- [PostgreSQL Official Documentation](https://www.postgresql.org/docs/)
- [Spring Boot with PostgreSQL](https://spring.io/guides/gs/accessing-data-jpa/)
- [Docker PostgreSQL Image](https://hub.docker.com/_/postgres)
- [Flyway Database Migrations](https://flywaydb.org/documentation/)
- [HikariCP Connection Pool](https://github.com/brettwooldridge/HikariCP)

---

**Questions or issues?** Check the troubleshooting section or review service logs for detailed error messages.
