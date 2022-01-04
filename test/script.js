const test = document.getElementById("test")
test.onclick = async () => {
    var res = await fetch("http://192.168.178.42:8080/getfiles")
    var data = await res.json()
    console.log(data)
}

const testpost = document.getElementById("testpost")
testpost.onclick = async () => {
    var res = await fetch("http://192.168.178.42:8080/postfiles",  {
        mod: '*cors',
        method: "POST",
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({path: "/home/uwe/movies 34"})
    })
    var data = await res.json()
    console.log(data)
}