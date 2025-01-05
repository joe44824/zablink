# Variables
APP_JAR=$(shell ls target/*.jar 2>/dev/null)
MVNW=./mvnw

# Install dependencies
install-dependencies:
	sudo apt update
	sudo apt install openjdk-17-jdk -y
	sudo apt install python3-pip -y
	pip3 install --upgrade ansible

# Install Ansible modules
install-ansible-modules:
	ansible-galaxy collection install -r src/main/resources/ansible/collection-requirements.yml

# Build the project
build:
	$(MVNW) clean package

# Run the app
run:
	@if [ -z "$(APP_JAR)" ]; then \
		echo "Application JAR not found. Please run 'make build' first."; \
		exit 1; \
	else \
		java -jar $(APP_JAR); \
	fi

# Clean project
clean:
	$(MVNW) clean
	rm -rf target/

# Combined setup and run
setup:
	make install-dependencies
	make install-ansible-modules
	make build
	make run
