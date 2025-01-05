# Variables
PYTHON_ENV=venv
ANSIBLE_REQUIREMENTS=src/main/resources/ansible/collection-requirements.yml

# Install dependencies
install-dependencies:
	sudo apt update
	sudo apt install -y openjdk-17-jdk python3-pip python3-venv nmap
	python3 -m venv $(PYTHON_ENV)
	. $(PYTHON_ENV)/bin/activate && pip install --upgrade pip ansible

# Install Ansible modules
install-ansible-modules:
	. $(PYTHON_ENV)/bin/activate && ansible-galaxy collection install -r $(ANSIBLE_REQUIREMENTS)

# Build the project
build:
	./mvnw clean package

# Run the application
run:
	@if [ ! -d "$(PYTHON_ENV)" ]; then \
		echo "Python environment not found. Please run 'make install-dependencies' first."; \
		exit 1; \
	fi
	@if [ ! -f target/*.jar ]; then \
		echo "Application JAR not found. Please run 'make build' first."; \
		exit 1; \
	fi
	java -jar target/*.jar

# Clean project
clean:
	./mvnw clean
	rm -rf target/
	rm -rf $(PYTHON_ENV)

# Combined setup and run
setup:
	make install-dependencies
	make install-ansible-modules
	make build
	make run
