#!/usr/bin/env python3
"""
Script to convert SQL Server functions to PostgreSQL compatible functions
"""

import os
import re

def fix_sql_functions(content):
    """Convert SQL Server functions to PostgreSQL compatible ones"""
    
    # Replace YEAR() with EXTRACT(YEAR FROM ...)
    content = re.sub(r'YEAR\(([^)]+)\)', r'EXTRACT(YEAR FROM \1)', content)
    
    # Replace MONTH() with EXTRACT(MONTH FROM ...)
    content = re.sub(r'MONTH\(([^)]+)\)', r'EXTRACT(MONTH FROM \1)', content)
    
    # Replace DAY() with EXTRACT(DAY FROM ...)
    content = re.sub(r'DAY\(([^)]+)\)', r'EXTRACT(DAY FROM \1)', content)
    
    return content

def process_java_files(directory):
    """Process all Java files in the directory"""
    
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith('.java'):
                file_path = os.path.join(root, file)
                
                try:
                    with open(file_path, 'r', encoding='utf-8') as f:
                        content = f.read()
                    
                    # Check if file contains SQL functions that need fixing
                    if any(func in content for func in ['YEAR(', 'MONTH(', 'DAY(']):
                        print(f"Fixing SQL functions in: {file_path}")
                        
                        # Apply fixes
                        new_content = fix_sql_functions(content)
                        
                        # Write back if changed
                        if new_content != content:
                            with open(file_path, 'w', encoding='utf-8') as f:
                                f.write(new_content)
                            print(f"  ✅ Updated {file_path}")
                        else:
                            print(f"  ℹ️  No changes needed in {file_path}")
                            
                except Exception as e:
                    print(f"  ❌ Error processing {file_path}: {e}")

if __name__ == "__main__":
    # Process src/main/java directory
    java_dir = "src/main/java"
    if os.path.exists(java_dir):
        print("🔧 Converting SQL Server functions to PostgreSQL compatible functions...")
        process_java_files(java_dir)
        print("✅ Conversion completed!")
    else:
        print(f"❌ Directory {java_dir} not found!")