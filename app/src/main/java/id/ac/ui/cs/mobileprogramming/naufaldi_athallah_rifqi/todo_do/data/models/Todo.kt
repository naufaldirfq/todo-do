package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models

data class Todo(var id: String,
                      var todo: String,
                      var completed: Boolean,
                      var date: String,
                      val user: String,
                      var createdAt: String)