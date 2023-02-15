function getDelete(str) {
    const no = document.querySelector('#number').value;
    alert(no);
    const result = confirm('삭제 하시겠습니까?');

    if(result) {
        axios
            .delete(str + no)
            .then(response => {
                alert('승인처리가 진행중인 경우, 삭제가 안될 수 있습니다.');
                location.href = "http://localhost:8889/payment/list";
            })
            .catch(err => {
                console.log(err)
            })
    }

}