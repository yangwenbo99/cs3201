* client => server message end with "\nEND\n"
* Server => client message start with #id: (/addr), and end with '\nEND\n'
* when front send message to back, there should not be "END", and when back send message, there should not be "END", too. However, the first line "#id: (/addr)" will be kept