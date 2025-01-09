//채팅방 추가
async function addRoom(roomRegisterObj) {
    console.log("저장할 데이터: "+roomRegisterObj)
    const response = await axios.post(`/chatingRoom/roomRegister`, roomRegisterObj);
    return response.data;
}
//채팅방 업데이트
async function UpdateRoom(roomInfoObj){
    const response = await axios.put(`/chatingRoom/${roomInfoObj.roomId}`,roomInfoObj)
    return response.data
}
//채팅방 삭제
async function deleteRoom(roomId) {
    const response = await axios.delete(`/chatingRoom/${roomId}`);  // Send roomId in the URL
    return response.data;
}
//채팅방 나가기
async function exitUser(exitObj){
    console.log(exitObj);
    const response = await axios.post(`/chatingRoom/exit`, exitObj);
    return response.data
}
//채팅 조회
async function getChatList(roomId){
    const result = await axios.get(`/chatingRoom/chatList/${roomId}`);
    return result.data
}
//채팅 작성
async function addMessage(messageObj){
    const result = await axios.post('/chatingRoom/messageRegister',messageObj)
    return result.data
}
//채팅 삭제
async function deleteMessage(messageId){
    console.log("삭제할 메시지 번호"+ messageId)
    const result = await  axios.delete(`/chatingRoom/messageDelete/${messageId}`);
    return result.data
}
// 유저 조회
async function getUserList(roomId, keyword) {
    console.log("키워드2 : " + keyword);
    const result = await axios.get(`/chatingRoom/userList/${roomId}`, {
        params: { keyword: keyword }  // keyword를 쿼리 파라미터로 전달
    });
    return result.data;
}
//채팅방 초대
async function inviteUser(inviteObj) {
    const response = await axios.post('/chatingRoom/invite', inviteObj);
    return response.data
}






