# Getting Started with BabashkaBins (BBB) for Way

This guide explains how to build the Way tool using the BabashkaBins (BBB) framework, which enables fast development with Babashka and native binary compilation with GraalVM.

## What is BabashkaBins?

BabashkaBins is a framework for building CLI tools in Clojure that can run under both Babashka and JVM Clojure, with support for compiling to native binaries using GraalVM. It solves the friction of building and distributing Clojure CLI tools by providing:

- **Fast Development**: Instant startup with Babashka during development
- **JVM Compatibility**: Can run on standard JVM Clojure  
- **Native Compilation**: Compiles to fast-starting static binaries
- **Professional CLI**: Uses cli-matic for argument parsing and help systems
- **Automatic Tooling**: GraalVM auto-installation and build automation

## Prerequisites

You'll need a Unix-like system (Linux/macOS) with:
- `curl` for downloading tools
- `git` for version control
- Internet connection for downloading dependencies

No need to install Java, Clojure, or GraalVM manually - BBB will handle this automatically.

## Setup Instructions

### 1. Clone the BabashkaBins Framework

First, you need the BBB framework itself. Clone it to a directory of your choice:

```bash
# Clone the BBB framework
git clone https://github.com/your-org/babashka-bins bbb
cd bbb
```

### 2. Install Required Tools

BBB needs several tools to work. Here's how to install them:

#### Install Babashka (Linux ARM64 example)
```bash
# Create local tools directory
mkdir -p local-bin

# Download and install Babashka (adjust URL for your platform)
curl -sLO https://github.com/babashka/babashka/releases/download/v1.12.203/babashka-1.12.203-linux-aarch64-static.tar.gz
tar -xzf babashka-1.12.203-linux-aarch64-static.tar.gz -C local-bin
```

For other platforms, check [Babashka releases](https://github.com/babashka/babashka/releases) and adjust the download URL.

#### Install Java via SDKMAN
```bash
# Install SDKMAN (Java version manager)
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install Java 11 (required for GraalVM)
sdk install java 11.0.17-tem
```

#### Install Clojure CLI Tools
```bash
# Download and install Clojure CLI
curl -O https://download.clojure.org/install/linux-install-1.11.1.1413.sh
chmod +x linux-install-1.11.1.1413.sh
./linux-install-1.11.1.1413.sh --prefix $(pwd)/clj-local
```

#### Install rlwrap (required for Clojure REPL)
On Ubuntu/Debian:
```bash
sudo apt-get update && sudo apt-get install -y rlwrap
```

On other systems, use your package manager to install `rlwrap`.

### 3. Set Up Environment

Create a script to set up your environment (save as `setup-env.sh`):

```bash
#!/bin/bash
# Setup environment for BBB development
export PATH="$(pwd)/clj-local/bin:$(pwd)/local-bin:$PATH"
source "$HOME/.sdkman/bin/sdkman-init.sh"
echo "BBB environment ready!"
echo "Babashka version: $(bb --version 2>/dev/null || echo 'not found')"
echo "Java version: $(java -version 2>&1 | head -1 || echo 'not found')"
echo "Clojure version: $(clj --version 2>/dev/null || echo 'not found')"
```

Make it executable and run it:
```bash
chmod +x setup-env.sh
source setup-env.sh
```

### 4. Install Way Source Code

Copy the Way source code into the BBB framework structure:

```bash
# Copy way source to BBB src directory
cp -r /path/to/way/src/way src/
```

Or if you have the way repo cloned separately:
```bash
# If way is in a sibling directory
cp -r ../way/src/way src/
```

### 5. Configure BBB for Way

Edit `bb.edn` and change the `MAIN-NS` to point to Way:

```clojure
{:tasks {:init (def MAIN-NS "way.main")
         ;; ... rest of config
```

The Way source has already been modified to work with BBB's cli-matic framework.

## Building and Running Way

Now you can build and run Way in multiple modes:

### Development Mode (Fast - uses Babashka)

```bash
# Source the environment first
source setup-env.sh

# Run Way with help
./local-bin/bb run --help

# Run specific Way commands
./local-bin/bb run notecheck
./local-bin/bb run shell bash
./local-bin/bb run show
```

This mode starts instantly and is perfect for development and testing.

### JVM Mode (Compatibility Testing)

```bash
# Test JVM Clojure compatibility
./local-bin/bb run-clj --help
./local-bin/bb run-clj notecheck
```

This ensures your code works on standard JVM Clojure, not just Babashka.

### Native Binary Compilation

This creates a fast-starting native executable:

```bash
# Set non-interactive mode (optional, prevents prompts)
export BBB_AUTOGRAAL_NOINTERACTIVE=true

# Compile to native binary (takes 2-3 minutes)
./local-bin/bb native-image
```

This will:
1. Auto-download and install GraalVM to `vendor/graalvm/`
2. Compile Way to a native binary named `bb`
3. The binary will be ~100MB but starts instantly

### Using the Native Binary

Once compiled, you can use the native binary directly:

```bash
# The compiled binary is named 'bb' in the project root
./bb --help
./bb notecheck
./bb shell bash
```

This binary is self-contained and can be distributed without requiring Java or Babashka on the target system.

## Troubleshooting

### "Could not locate bbb/core" Error
This means you're not running from the BBB framework directory or the environment isn't set up. Make sure you:
1. Are in the BBB framework directory (not the original way directory)
2. Have sourced the environment: `source setup-env.sh`
3. Are using `./local-bin/bb run` not direct Babashka

### "Couldn't find 'java'" Error
You need to source the SDKMAN environment:
```bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
```

### Native Image Compilation Fails
1. Ensure you have enough RAM (4GB+ recommended)
2. Make sure Java 11+ is installed and JAVA_HOME is set
3. Check that GraalVM auto-installed correctly in `vendor/graalvm/`

### Permission Errors
If you get permission errors installing rlwrap or other system tools, you may need to use `sudo` or install to user directories.

## Development Workflow

1. **Make Changes**: Edit source code in `src/way/`
2. **Test Fast**: Use `./local-bin/bb run` for instant feedback
3. **Test Compatibility**: Use `./local-bin/bb run-clj` to ensure JVM compatibility
4. **Build Binary**: Use `./local-bin/bb native-image` when ready to distribute

## Project Structure

```
bbb/                           # BBB framework root
├── src/way/                   # Way source code
│   ├── main.clj              # Entry point (uses cli-matic)
│   ├── notecheck.clj         # Core functionality
│   └── ...                   # Other Way modules
├── local-bin/                # Local tool installations
│   ├── bb                    # Babashka binary
│   └── clj                   # Clojure CLI wrapper
├── clj-local/                # Clojure CLI installation
├── vendor/graalvm/           # Auto-installed GraalVM
├── bb.edn                    # BBB configuration
├── deps.edn                  # Clojure dependencies
└── bb                        # Compiled native binary (after build)
```

## Next Steps

- Read the [BabashkaBins documentation](CLAUDE.md) for advanced usage
- Explore the BBB framework source code to understand how it works
- Consider contributing improvements back to the Way project

## Getting Help

If you encounter issues:
1. Check this guide's troubleshooting section
2. Examine the BBB framework documentation in `CLAUDE.md`
3. Look at the commit history to see how Way was integrated
4. Open an issue in the appropriate repository

The integration maintains full compatibility with the original Way functionality while adding the benefits of the BBB framework for development and distribution.