 
listView('project Jobs') {
    description('project Jobs')
    jobs {
        regex('project_.+')
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
