import os
import glob

directory = r'd:\PROJECT\P2_RevPlay_Baburao\src\main\resources\templates'
files = glob.glob(os.path.join(directory, '*.html'))

for file in files:
    with open(file, 'r', encoding='utf-8') as f:
        content = f.read()

    # Skip files that already have the queue link or don't have a history link
    if 'th:href="@{/queue}"' in content or 'th:href="@{/history}"' not in content:
        continue

    # Find the history link and append queue
    new_content = content.replace(
        '<a class="nav-link" th:href="@{/history}">History</a></li>',
        '<a class="nav-link" th:href="@{/history}">History</a></li>\n                    <li class="nav-item"><a class="nav-link fw-bold text-info" th:href="@{/queue}">Queue</a></li>'
    )
    
    if content != new_content:
        with open(file, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f'Updated {os.path.basename(file)}')
