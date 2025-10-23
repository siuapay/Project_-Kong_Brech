#!/usr/bin/env python3
"""
Script to convert EXTRACT functions to MySQL compatible YEAR() and MONTH() functions
"""

import os
import re

def fix_mysql_functions(file_path):
    """Fix EXTRACT functions in a Java file for MySQL compatibility"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        
        # Replace EXTRACT(YEAR FROM ...) with YEAR(...)
        content = re.sub(r'EXTRACT\(YEAR FROM ([^)]+)\)', r'YEAR(\1)', content)
        
        # Replace EXTRACT(MONTH FROM ...) with MONTH(...)
        content = re.sub(r'EXTRACT\(MONTH FROM ([^)]+)\)', r'MONTH(\1)', content)
        
        if content != original_content:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"✅ Fixed: {file_path}")
            return True
        else:
            print(f"⏭️  No changes needed: {file_path}")
            return False
            
    except Exception as e:
        print(f"❌ Error processing {file_path}: {e}")
        return False

def main():
    """Main function to process all repository files"""
    repository_dir = "src/main/java/com/branch/demo/repository"
    
    if not os.path.exists(repository_dir):
        print(f"❌ Repository directory not found: {repository_dir}")
        return
    
    print("🔧 Converting EXTRACT functions to MySQL compatible YEAR() and MONTH() functions...")
    print("=" * 80)
    
    fixed_count = 0
    total_count = 0
    
    for filename in os.listdir(repository_dir):
        if filename.endswith('.java'):
            file_path = os.path.join(repository_dir, filename)
            total_count += 1
            if fix_mysql_functions(file_path):
                fixed_count += 1
    
    print("=" * 80)
    print(f"📊 Summary: Fixed {fixed_count} out of {total_count} repository files")
    print("🎉 MySQL function conversion completed!")

if __name__ == "__main__":
    main()