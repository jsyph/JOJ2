import sqlite3
import json
import os
import re
import argparse
from tqdm import tqdm

# --- CONFIGURATION ---
DB_NAME = "joj.db"
JSONL_FILE = "problems.jsonl"
REPO_FOLDERS = ["CSES-in-Python", "CSES-Solutions"]

# Categories to include
ALLOWED_CATEGORIES = {
    "Introductory Problems",
    # "Sorting and Searching",
    # "Dynamic Programming",
    # "Graph Algorithms",
    # "Range Queries",
    # "Tree Algorithms",
    # "Mathematics",
    # "String Algorithms",
    # "Geometry",
    # "Advanced Techniques",
}

HEADER = r"""
     ██╗ ██████╗      ██╗    ██████╗ ██████╗      ██████╗ ███████╗███╗   ██╗
     ██║██╔═══██╗     ██║    ██╔══██╗██╔══██╗    ██╔════╝ ██╔════╝████╗  ██║
     ██║██║   ██║     ██║    ██║  ██║██████╔╝    ██║  ███╗█████╗  ██╔██╗ ██║
██   ██║██║   ██║██   ██║    ██║  ██║██╔══██╗    ██║   ██║██╔══╝  ██║╚██╗██║
╚█████╔╝╚██████╔╝╚█████╔╝    ██████╔╝██████╔╝    ╚██████╔╝███████╗██║ ╚████║
 ╚════╝  ╚═════╝  ╚════╝     ╚═════╝ ╚═════╝      ╚═════╝ ╚══════╝╚═╝  ╚═══╝
        By Youssef Khaled
"""


def parse_val(s):
    m = re.search(r"(\d+)", str(s))
    return int(m.group(1)) if m else 0


def get_title_variants(filename):
    base = os.path.splitext(filename)[0]
    return {base, base.replace("_", " ").strip(), base.replace(" ", "_").strip()}


def init_db():
    conn = sqlite3.connect(DB_NAME)
    curr = conn.cursor()
    curr.execute("PRAGMA foreign_keys = ON;")
    curr.executescript(
        """
        CREATE TABLE IF NOT EXISTS problems (
            id INTEGER PRIMARY KEY,
            title TEXT UNIQUE,
            content TEXT,
            time_ms INTEGER,
            memory_mb INTEGER,
            category TEXT
        );
        CREATE TABLE IF NOT EXISTS solutions (
            id INTEGER PRIMARY KEY,
            problem_id INTEGER,
            lang TEXT,
            source_code TEXT,
            FOREIGN KEY(problem_id) REFERENCES problems(id) ON DELETE CASCADE
        );
        CREATE TABLE IF NOT EXISTS testcases (
            id INTEGER PRIMARY KEY,
            problem_id INTEGER,
            input_data TEXT,
            output_data TEXT,
            FOREIGN KEY(problem_id) REFERENCES problems(id) ON DELETE CASCADE
        );
    """
    )
    conn.commit()
    return conn


def run_sync(import_limit):
    print(HEADER)
    conn = init_db()
    curr = conn.cursor()

    # Step 1: Filtered Problems & Testcases
    print(f"[*] Scanning {JSONL_FILE} for specific categories...")

    imported_count = 0
    # Use a manual loop because we don't know when we'll hit the limit with filtering
    with open(JSONL_FILE, "r", encoding="utf-8") as f:
        pbar = tqdm(total=import_limit, desc="Importing Problems", unit="prob")

        for line in f:
            if imported_count >= import_limit:
                break

            data = json.loads(line)
            category = data.get("category")

            # --- THE FILTER ---
            if category not in ALLOWED_CATEGORIES:
                continue

            title = data.get("title")
            curr.execute(
                """INSERT OR IGNORE INTO problems 
                (title, content, time_ms, memory_mb, category) 
                VALUES (?,?,?,?,?)""",
                (
                    title,
                    data.get("problem_statement"),
                    parse_val(data.get("time_limit")),
                    parse_val(data.get("memory_limit")),
                    category,
                ),
            )

            p_id_row = curr.execute(
                "SELECT id FROM problems WHERE title=?", (title,)
            ).fetchone()
            if p_id_row:
                p_id = p_id_row[0]
                tcs = [
                    (p_id, t.get("input"), t.get("output"))
                    for t in data.get("test_cases", [])
                ]
                curr.executemany(
                    "INSERT INTO testcases (problem_id, input_data, output_data) VALUES (?,?,?)",
                    tcs,
                )

                imported_count += 1
                pbar.update(1)
        pbar.close()

    conn.commit()

    # Step 2: Solution Crawling
    print("\n[*] Crawling repositories for source code...")
    all_files = []
    for folder in REPO_FOLDERS:
        if os.path.exists(folder):
            for root, _, files in os.walk(folder):
                for file in files:
                    if file.endswith((".py", ".cpp")):
                        all_files.append(os.path.join(root, file))

    linked_count = 0
    for fpath in tqdm(all_files, desc="Linking Solutions", unit="file"):
        fname = os.path.basename(fpath)
        variants = get_title_variants(fname)
        lang = "Python" if fname.endswith(".py") else "C++"

        placeholders = ", ".join(["?"] * len(variants))
        match = curr.execute(
            f"SELECT id FROM problems WHERE title IN ({placeholders})", list(variants)
        ).fetchone()

        if match:
            with open(fpath, "r", encoding="utf-8", errors="ignore") as f:
                code = f.read()
            curr.execute(
                "INSERT INTO solutions (problem_id, lang, source_code) VALUES (?, ?, ?)",
                (match[0], lang, code),
            )
            linked_count += 1

    conn.commit()
    conn.close()
    print(f"\n[+] Filtered Sync Complete.")
    print(f"[+] Categories: {', '.join(ALLOWED_CATEGORIES)}")
    print(f"[+] Problems Loaded: {imported_count}")
    print(f"[+] Solutions Linked: {linked_count}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Filtered CSES Sync Tool")
    parser.add_argument("--limit", type=int, default=30, help="Max problems to load")
    args = parser.parse_args()
    run_sync(args.limit)
