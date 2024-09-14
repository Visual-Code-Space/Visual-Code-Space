#  This file is part of Visual Code Space.
#
#  Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
#  the GNU General Public License as published by the Free Software Foundation, either version 3 of
#  the License, or (at your option) any later version.
#
#  Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
#  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License along with Visual Code Space.
#  If not, see <https://www.gnu.org/licenses/>.

import requests
import json

GITHUB_API_URL = "https://api.github.com/repos/{owner}/{repo}/contents/{path}"


"""
This is a test script..
"""
def get_repo_contents(owner, repo, path=""):
    """
    Fetches the contents of a GitHub repository directory
    :param owner: The owner of the repository
    :param repo: The repository name
    :param path: The path to the directory in the repo (empty string for root)
    :return: A list of directory and file information
    """
    url = GITHUB_API_URL.format(owner=owner, repo=repo, path=path)
    response = requests.get(url)

    if response.status_code == 200:
        return response.json()  # Return the directory contents as a list of dicts
    else:
        return {"error": f"Unable to fetch contents of {path} (HTTP {response.status_code})"}

def get_directories_and_files(owner, repo, path=""):
    """
    Get only directories and files with download links and sizes.
    :param owner: The owner of the repository
    :param repo: The repository name
    :param path: The path to the directory in the repo (empty string for root)
    :return: A JSON string with directories and file info
    """
    contents = get_repo_contents(owner, repo, path)
    result = []

    if isinstance(contents, list):  # Only proceed if we get a valid list of contents
        for item in contents:
            if item['type'] == 'dir':
                result.append({
                    "type": "directory",
                    "name": item['name']
                })
            elif item['type'] == 'file':
                result.append({
                    "type": "file",
                    "name": item['name'],
                    "download_url": item['download_url'],
                    "size": item['size']
                })

    return json.dumps(result, indent=2)
