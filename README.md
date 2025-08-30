# Jerrymouse

![Build Status](https://github.com/michaelliao/jerrymouse/actions/workflows/maven.yml/badge.svg)

![logo](logo.png)

# Overview

Jerrymouse is a lightweight servlet container that implements the Jakarta Servlet 6 specification. It serves as a minimalist version of Apache Tomcat, focusing on core servlet functionality while maintaining a straightforward architecture that's easy to understand.

[Download](https://github.com/michaelliao/jerrymouse/releases)

# Purpose and Scope

Jerrymouse demonstrates the essential concepts of a servlet container in a simplified manner, making it valuable for educational purposes and for understanding how servlet containers work. It implements the core parts of the Jakarta Servlet 6 specification while intentionally excluding more complex features to maintain clarity.

# Key Features and Limitations

Jerrymouse implements a focused subset of the Jakarta Servlet 6 standard:

- Servlet support;
- Filter support;
- Listener support;
- HttpSession support (Cookie-mode only).

Unsupported features:

- Not support JSP;
- Not support async;
- Not support WebSocket.

Additionally, Jerrymouse has the following deployment constraints:

- Supports deploying a single standard web application;
- Does not support multiple web application deployment;
- Does not support hot reloading of applications.

# Tutorial

[Chinese Tutorial](https://liaoxuefeng.com/books/jerrymouse/)
