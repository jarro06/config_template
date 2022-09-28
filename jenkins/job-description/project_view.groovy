 
listView('PROJECT Jobs') {
    description('PROJECT Jobs')
    jobs {
        regex('PROJECT_.+')
    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}
