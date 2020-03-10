// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['I adopted a very playful puppy recently!', 'I have watched Breaking Bad like 4 times', 'I also go by the nickname Memazo', 'I have 2 tortoises as pets', 'I was born exactly one year after my sister', 'I once solved the 3x3 Rubiks cube one-handed', 'I once won a Clue game in a single play!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

function requestTranslation(){
    //Clears the comment container so comments are not duplicated,
    //but instead replaced by themselves in the selected language
    document.getElementById('commentContainer').innerText = '';
    //Gets the selected language code from the dropdown
    const languageCode = document.getElementById('languageCode').value;
    getCommentSection(languageCode);
}

//Language is in English by default
function getCommentSection(languageCode='en'){
    const params = new URLSearchParams();
    params.append('languageCode', languageCode);
    
    //www.example.com/file?key=value --> languageCode=en/es/de...
    fetch('/data?' + params.toString()).then(response => response.json()).then((comments) => {
        const divElement = document.getElementById('commentContainer');
        comments.forEach((comment) => {
            divElement.appendChild(createCommentDiv(comment));
        });
    });
}

function createCommentDiv(comment){
    //Where the author, comment, and timestamp will go
    const myDiv = document.createElement('div');

    //<p><b>Author</b></p>
    const pAuthorElement = document.createElement('p'); 
    const boldAuthorElement = document.createElement('B');
    boldAuthorElement.innerText = comment.author;
    pAuthorElement.appendChild(boldAuthorElement);
    
    //<p>Comment</p>
    const pCommentElement = document.createElement('p');
    pCommentElement.innerText = comment.comment;

    //<p>Timestamp: timestamp</p>
    const pTimestampElement = document.createElement('p');
    pTimestampElement.innerText = 'Timestamp: ' + comment.timestamp;

    //Placing all the above info in the <div> tag, and adding <br /> and <hr> tags at the end
    myDiv.appendChild(pAuthorElement);
    myDiv.appendChild(pCommentElement);
    myDiv.appendChild(pTimestampElement);
    myDiv.appendChild(document.createElement('br'));
    myDiv.appendChild(document.createElement('hr'));

    return myDiv;
}