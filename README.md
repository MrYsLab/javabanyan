

# javabanyan
This is a preliminary version of the  Java compatible version of the [Python Banyan Framework](https://mryslab.github.io/python_banyan/).

Banyan is a lightweight, reactive framework used to create flexible, non-blocking, 
event driven, asynchronous applications. It was designed primarily to 
implement physical computing applications for devices such as the 
Raspberry Pi and Arduino, but it is not limited to just the physical computing domain, 
and may be used to create applications in any domain.

Banyan applications are comprised of a set of components, each component being a seperate process. 
Components communicate with each other by publishing and subscribing to language independent protocol messages.
As a result, any component can communicate with any other component, regardless of computer language.
Each Banyan component connects to a common Banyan backplane that distributes published messages to all message
subscribers.

[Documentation]((http://htmlpreview.github.io/?https://github.com/MrYsLab/javabanyan/blob/master/documentation/index.html)