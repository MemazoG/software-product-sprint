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

package com.google.sps.servlets;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Task;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //-----DATASTORE-----
    Query query = new Query("Task").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    
    String languageCode = request.getParameter("languageCode");
    
    List<Task> tasks = new ArrayList<>();
    for(Entity entity : results.asIterable()){
        long id = entity.getKey().getId();
        String author = (String) entity.getProperty("author");
        String comment = (String) entity.getProperty("comment");
        long timestamp = (long) entity.getProperty("timestamp");

        Translate translate = TranslateOptions.getDefaultInstance().getService();
        Translation translation = translate.translate(comment, Translate.TranslateOption.targetLanguage(languageCode));
        String translatedComment = translation.getTranslatedText();

        //Replaces the comment in english with the comment in the selected language
        Task task = new Task(id, author, translatedComment, timestamp);
        tasks.add(task);
    }
    Gson gson = new Gson();
    String json = gson.toJson(tasks);
    response.setContentType("application/json");
    response.getWriter().println(json);
  }


  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //Gets the information
    String commentAuthor = getParameter(request, "textName", "Anonymous");
    String comment = getParameter(request, "textComment", "-");
    long timestamp = System.currentTimeMillis();

    Entity taskEntity = new Entity("Task");

    taskEntity.setProperty("author", commentAuthor);
    taskEntity.setProperty("comment", comment);
    taskEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);

    //Redirecting to the same page
    response.sendRedirect("/index.html");
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}