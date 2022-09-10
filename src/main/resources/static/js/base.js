const  toggleSidebar = ()=> {
    if ($(".sidebar").is(":visible")){
           $(".sidebar").css("display","none");
           $(".content").css("margin-left","0%");
          
    }else{
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");
       
    }
};



const search = ()=>{
		
	let query=$("#serach_input").val();
	
	if(query==''){
			$(".search_result").hide();
	}else{
	console.log(query);
	
	//send request server
	let url=`http://localhost:8082/search/${query}`;
	
	fetch(url).then(
		(response)=>{
			return response.json();			
		}).then(
			(data)=>{
			//	console.log(data); /user/33/contact_details
			
				let text=`<div class='list-group'>`;
				
				data.forEach((contact)=>{
					text+=`<a  href='/user/${contact.cid}/contact_details'
					 class='list-group-item list-group-item-action'>${contact.name}</a>`;
				});
				
				text+=`</div>`;
				$(".search_result").html(text);
				$(".search_result").show();
				
		})

};

//payment service
//first request to server to create order


function payment  (){
		return "Hi";
}

	const paymentStart = ()=>{
			let amount=$("#payment_field").val();
				console.log(amount);
				
			if(amount=='' || amount==null){
				alert("amount is requied")
				return;
			}
			
			//ajax to send request to create order
			$.ajax({
				url:'/user/create_order',
				data:JSON.stringify({amount:amount,info:'order_request'}),
				contentType:'application/json',
				type:'POST',
				dataType:'json',
				success:function(response){
					//invoked when success
					console.log(response);
				},
				error:function(error){
					console.log(error);
					alert("something went wrong !!");
				}
				
			})
				
				
	};
	
	
}