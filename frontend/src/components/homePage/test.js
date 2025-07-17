async function run() {
    const result = await fetchData(); // fetchData가 Promise 반환
    console.log(result);
  }
  


 function run() {
    const result = fetchData();
    console.log(result);
 }


 
 async function run() {
    const result = await fetchData();
    console.log(result);
 }

 const r = await run();