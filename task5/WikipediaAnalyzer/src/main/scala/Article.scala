class Article {
  var title: String = _
  var text: String = _
  var size: String = _
  var contributor: String = _
  var lastModified: String = _
}

object Article {
  def apply(title: String, text: String, size: String, contributor: String, lastModified: String): Article = {
    var a = new Article
    a.title = title
    a.text = text
    a.size = size
    a.contributor = contributor
    a.lastModified = lastModified
    a
  }
}