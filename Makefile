# Variables
PYTHON_ENV=venv
ANSIBLE_REQUIREMENTS=src/main/resources/ansible/collection-requirements.yml
SSH_KEY_PATH=/root/.ssh/zablink-joex42

# Install dependencies
install-dependencies:
	sudo apt update
	sudo apt install -y openjdk-17-jdk python3-pip python3-venv nmap ansible
	python3 -m venv $(PYTHON_ENV)
	. $(PYTHON_ENV)/bin/activate && pip install --upgrade pip ansible

# Validate Python environment
validate-env:
	@if [ ! -d "$(PYTHON_ENV)" ]; then \
		echo "Python environment not found. Please run 'make install-dependencies' first."; \
		exit 1; \
	fi

# Install Ansible modules
install-ansible-modules: validate-env
	. $(PYTHON_ENV)/bin/activate && ansible-galaxy collection install -r $(ANSIBLE_REQUIREMENTS)

# Set up SSH private key
setup-ssh-key:
	@echo "Setting up SSH private key..."
	@echo "-----BEGIN OPENSSH PRIVATE KEY-----\n\
b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAAAMwAAAAtzc2gtZW\n\
QyNTUxOQAAACCFAu7rTjkzd2E/berRay/Gyanl498kUzOgjWFo/ERJ8wAAAKg0IGKMNCBi\n\
jAAAAAtzc2gtZWQyNTUxOQAAACCFAu7rTjkzd2E/berRay/Gyanl498kUzOgjWFo/ERJ8w\n\
AAAEAxza/Sq5OmoXJdanj8Imh3Kxm7Fl8t+QfxG3K/3EL/wIUC7utOOTN3YT9t6tFrL8bJ\n\
qeXj3yRTM6CNYWj8REnzAAAAIGhhYmliaUBIYWJpYmlzLU1hY0Jvb2stUHJvLmxvY2FsAQ\n\
IDBAU=\n\
-----END OPENSSH PRIVATE KEY-----" > $(SSH_KEY_PATH)
	@chmod 600 $(SSH_KEY_PATH)
	@echo "SSH private key set up at $(SSH_KEY_PATH)."

# Build the project
build:
	./mvnw clean package

# Run the application
run: validate-env
	@if [ ! -f target/*.jar ]; then \
		echo "Application JAR not found. Please run 'make build' first."; \
		exit 1; \
	fi
	java -jar target/*.jar

# Restart the application
restart:
	@echo "Stopping running application..."
	@pkill -f 'java -jar' || echo "No running application found."
	@make run

# Pull the latest code and restart
pull-and-restart:
	@echo "Pulling the latest code from Git..."
	@git pull origin main
	@make restart

# Clean project
clean:
	./mvnw clean
	rm -rf target/
	rm -rf $(PYTHON_ENV)

# Combined setup and run
setup:
	make install-dependencies
	make install-ansible-modules
	make setup-ssh-key
	make build
	make run
