window.addEventListener('DOMContentLoaded', () => {

    const file  = document.querySelector("#file");
    // file 개수 체크
    file.addEventListener("change", function() {
        let files = document.getElementById("file");
        if(files.files.length > 3) {
            alert("파일은 최대 3개까지 업로드 할 수 있습니다.")
            files.value = "";
        }
    });

    const formBody  = document.querySelector("#formBody");
    const btnSend   = document.querySelector("#btnSend");
    const title = document.querySelector("#title");
    const receiveName = document.querySelector("#receiveName");
    const content = document.querySelector("#content");

    // 쪽지쓰기 버튼
    btnSend.addEventListener("click", function(e) {
        e.preventDefault();

        if(title.value == '') { alert("제목을 입력해 주세요."); return;}
        if(receiveName.value == '') { alert("수신자를 선택해 주세요."); return; }
        if(content.value == '') { alert("내용을 입력해 주세요."); return; }

        let refer = document.referrer;
        let referArray = refer.split('/');
        let url = referArray[referArray.length-1];
        console.log(url);
        const result = confirm("보내시겠습니까?")
        if(result) {
            formBody.action = '/message/sendMessage?url='+url;
            formBody.method = 'post';
            formBody.submit();
        }
    });

});