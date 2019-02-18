package com.outjected.email.impl.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public class HtmlToPlainText {

    public static String convert(String html) {
        Document doc = Jsoup.parse(html);
        HtmlToPlainText formatter = new HtmlToPlainText();
        return formatter.getPlainText(doc);
    }

    /**
     * Format an Element to plain-text
     *
     * @param element the root element to format
     * @return formatted text
     */
    private String getPlainText(Element element) {
        FormattingVisitor formatter = new FormattingVisitor();
        NodeTraversor.traverse(formatter, element); // walk the DOM, and call .head() and .tail() for each node
        return formatter.toString();
    }

    // the formatting rules, implemented in a breadth-first DOM traverse
    private static class FormattingVisitor implements NodeVisitor {
        private static final int maxWidth = 80;
        private int width = 0;
        private StringBuilder accum = new StringBuilder(); // holds the accumulated text

        // hit when the node is first seen
        @Override public void head(Node node, int depth) {
            String name = node.nodeName();
            if (node instanceof TextNode) {
                if (node.parent().nodeName().equals("title")) {
                    //skip title;
                    return;
                }
                append(((TextNode) node).text()); // TextNodes carry all user-readable text in the DOM.
            }
            else if (name.equals("li")) {
                append("\n * ");
            }
            else if (name.equals("dt")) {
                append("  ");
            }
            else if (Strings.in(name, "p", "h1", "h2", "h3", "h4", "h5", "tr")) {
                append("\n");
            }
        }

        // hit when all of the node's children (if any) have been visited
        @Override public void tail(Node node, int depth) {
            String name = node.nodeName();
            if (Strings.in(name, "br", "dd", "dt", "p", "h1", "h2", "h3", "h4", "h5", "div")) {
                append("\n");
            }
            else if (name.equals("a")) {
                append(String.format(" (%s)", node.absUrl("href")));
            }
        }

        // appends text to the string builder with a simple word wrap method
        private void append(String text) {
            if (accum.length() == 0 && Strings.isNullOrBlank(text)) {
                return;
            }

            if (text.startsWith("\n")) {
                width = 0; // reset counter if starts with a newline. only from formats above, not in natural text
            }
            if (text.equals(" ") && (accum.length() == 0 || Strings.in(accum.substring(accum.length() - 1), " ", "\n"))) {
                return; // don't accumulate long runs of empty spaces
            }

            if (text.length() + width > maxWidth) { // won't fit, needs to wrap
                String[] words = text.split("\\s+", -1);
                for (int i = 0; i < words.length; i++) {
                    String word = words[i];
                    boolean last = i == words.length - 1;
                    if (!last) // insert a space if not the last word
                    {
                        word = word + " ";
                    }
                    if (word.length() + width > maxWidth) { // wrap and reset counter
                        accum.append("\n").append(word);
                        width = word.length();
                    }
                    else {
                        accum.append(word);
                        width += word.length();
                    }
                }
            }
            else { // fits as is, without need to wrap text
                accum.append(text);
                width += text.length();
            }
        }

        @Override public String toString() {
            return accum.toString();
        }
    }
}
