async function addRoom(roomRegisterObj) {
    const response = await axios.post(`/matchingRoom/roomRegister`, roomRegisterObj);
    return response.data;
}
//업데이트
async function UpdateRoom(roomInfoObj){
    const response = await axios.put(`/matchingRoom/${roomInfoObj.roomId}`,roomInfoObj)
    return response.data
}

//삭제
async function deleteRoom(roomId) {
    const response = await axios.delete(`/matchingRoom/${roomId}`);  // Send roomId in the URL
    return response.data;
}

async function updateAndDelete(exitObj){
    await axios.post(`/matchingRoom/roomUAD`, exitObj);
    //return response.data
}









