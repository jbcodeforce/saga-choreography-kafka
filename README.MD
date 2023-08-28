# [Saga Choreography demonstration with Kafka](https://jbcodeforce.github.io/saga-choreography-kafka)


Read [in mkdocs format](https://jbcodeforce.github.io/saga-choreography-kafka).

A repository to demonstrate a Saga with service choreography using Apache Kafka.



### Building this booklet locally

The content of this repository is written with markdown files, packaged with [MkDocs](https://www.mkdocs.org/) and can be built into a book-readable format by MkDocs build processes.

1. Install MkDocs locally following the [official documentation instructions](https://www.mkdocs.org/#installation).
2. `git clone https://github.com/jbcodeforce/saga-choreography-kafka.git` _(or your forked repository if you plan to edit)_
3. `cd saga-choreography-kafka`
4. `mkdocs serve`
5. Go to `http://127.0.0.1:8000/` in your browser.

### Pushing the book to GitHub Pages

1. Ensure that all your local changes to the `master` branch have been committed and pushed to the remote repository.
   1. `git push origin master`
2. Ensure that you have the latest commits to the `gh-pages` branch, so you can get others' updates.
	```bash
	git checkout gh-pages
	git pull origin gh-pages
	
	git checkout main
	```
3. Run `mkdocs gh-deploy` from the root saga-choreography-kafka directory.

--- 

## Contributing

Please create a new GitHub issue for any feature requests, bugs, or documentation improvements.

Where possible, please also submit a pull request for the change.

**Contributors:**

* [Jerome Boyer](https://www.linkedin.com/in/jeromeboyer/)

## License

This library is licensed under the MIT-0 License. See the LICENSE file.