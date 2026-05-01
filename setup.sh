#!/bin/bash

echo "Creating N3xion project structure..."

# Create directory structure
mkdir -p app/src/main/java/com/n3xion/{ui/adapters,data/database/entities,data/repository,viewmodel}
mkdir -p app/src/main/res/{layout,values,drawable,menu,mipmap-{h,m,x,xx,xxx}dpi}
mkdir -p .github/workflows

echo "Directory structure created!"
echo "Ready for file creation. Continue to Part 2."
