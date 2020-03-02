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

function getCommentSection(){
    fetch('/data').then(response => response.json()).then((comments) => {
        for(let i=0; i<comments.length; i++){
            let textAuthor = '<p><b>' + comments[i].author + '</b></p>';
            let textComment = '<p>' + comments[i].comment + '</p>';
            let textTimestamp = '<p> Timestamp: ' + comments[i].timestamp + '</p></br><hr>';

            document.getElementById('commentContainer').innerHTML += textAuthor;
            document.getElementById('commentContainer').innerHTML += textComment;
            document.getElementById('commentContainer').innerHTML += textTimestamp;
        }
    });
}