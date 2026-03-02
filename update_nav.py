import os
import glob
import re

directory = r'd:\PROJECT\P2_RevPlay_Baburao\src\main\resources\templates'
files = glob.glob(os.path.join(directory, '*.html'))

pattern1 = re.compile(r'(\s*<li class="nav-item"\s*sec:authorize="hasRole\(\'ARTIST\'\)">(?:\s|.)*?<a class="nav-link"[^>]*th:href="@\{/my-albums\}"[^>]*>My Albums</a>\s*</li>)')
pattern2 = re.compile(r'(\s*<li class="nav-item">(?:\s|.)*?<a class="nav-link"\s*sec:authorize="hasRole\(\'ARTIST\'\)"\s*th:href="@\{/my-albums\}"[^>]*>My Albums</a>\s*</li>)')

for file in files:
    with open(file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    if '@{/artist/dashboard}' not in content and 'My Albums' in content:
        new_content = content
        
        def replacer1(m):
            lines = m.group(1).split('\n')
            indent = '\n' + (' ' * (len(lines[1]) - len(lines[1].lstrip())))
            nav_item = f'{indent}<li class="nav-item" sec:authorize="hasRole(\'ARTIST\')">{indent}    <a class="nav-link" th:href="@{{/artist/dashboard}}">Analytics</a>{indent}</li>'
            return nav_item + m.group(1)
            
        def replacer2(m):
            lines = m.group(1).split('\n')
            indent = '\n' + (' ' * (len(lines[1]) - len(lines[1].lstrip())))
            nav_item = f'{indent}<li class="nav-item">{indent}    <a class="nav-link" sec:authorize="hasRole(\'ARTIST\')" th:href="@{{/artist/dashboard}}">Analytics</a>{indent}</li>'
            return nav_item + m.group(1)

        new_content = pattern1.sub(replacer1, new_content)
        new_content = pattern2.sub(replacer2, new_content)
        
        if content != new_content:
            with open(file, 'w', encoding='utf-8') as f:
                f.write(new_content)
            print(f'Updated {os.path.basename(file)}')
