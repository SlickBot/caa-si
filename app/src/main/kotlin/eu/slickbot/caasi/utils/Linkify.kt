package eu.slickbot.caasi.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration

private val LINK_REGEX = Regex(
  pattern = """(https?://\S+)|(\b[\w.+-]+@[\w.-]+\.[A-Za-z]{2,}\b)""",
)

fun String.linkified(linkColor: Color): AnnotatedString {
  val text = this
  return buildAnnotatedString {
    val linkStyles = TextLinkStyles(
      style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
    )
    var cursor = 0
    for (match in LINK_REGEX.findAll(text)) {
      if (match.range.first > cursor) append(text.substring(cursor, match.range.first))
      val raw = match.value
      val url = if (raw.contains("://")) raw else "mailto:$raw"
      withLink(LinkAnnotation.Url(url, styles = linkStyles)) {
        append(raw)
      }
      cursor = match.range.last + 1
    }
    if (cursor < text.length) append(text.substring(cursor))
  }
}

private inline fun AnnotatedString.Builder.withLink(
  link: LinkAnnotation,
  block: AnnotatedString.Builder.() -> Unit,
) {
  val index = pushLink(link)
  try {
    block()
  } finally {
    pop(index)
  }
}
