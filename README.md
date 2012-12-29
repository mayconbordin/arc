# GitHub API Example #

```java
Arc arc = new Arc("https://api.github.com");

// list user repos
Response resp = arc.request("/users/joyent/repos")
		   .param("page", 1)
		   .param("per_page", 10)
		   .param("callback", "foo")
		   .get();

// star a repo
resp = arc.request("/user/starred/documentcloud/backbone")
	  .put();

// unstar a repo
resp = arc.request("/user/starred/documentcloud/backbone")
	  .delete();
```

## Basic Auth ##

```java
Arc arc = new Arc("https://api.github.com");
arc.setBasicAuth("username", "password");
```

## Multipart ##

```java
Response resp = arc.request("/post/an/image")
		   .byteArray("image", imageByteArray)
		   .post();
```

## Parameters and Headers ##

You can set a parameter or header one by one:

```java
Response resp = arc.request("/user/repos")
		   .param("name", "sample-project")
		   .header("Authorization", "OAUTH-TOKEN")
		   .post();
```

Or you can supply multiple key/value pairs at once using a Map<String, String> or an String[][] using the methods `params` and `headers`:

```java
Response resp = arc.request("/user/repos")
		   .params(new String[][] {
			   {"name", "sample-project"},
			   {"description", "Just a sample project"}
		    })
		   .header("Authorization", "OAUTH-TOKEN")
		   .post();
```
