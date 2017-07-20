# work.bill-scrapper

This is a program writen during work to parse the bill provided by CSL. The eletronic bill provided by CSL is in PDF format, and there is no way to import the data into a database for record keeping purposes. The CSV provided by the customer servies is illegible, so I figure that it might be easier to write a program to do it for me.

![Relevent XKCD](https://imgs.xkcd.com/comics/automation.png)

- The program parse the PDF using a PDF parser into text.
- Different part of the text is processed by my parsers.
- The result is inserted into a MySQL database.
