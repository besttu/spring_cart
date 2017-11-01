var TT = TAOTAO = {
	checkLogin : function() {
		var _ticket = $.cookie("TOKEN_KEY");
		if (!_ticket) {
			return;
		}
		$
				.ajax({
					url : "http://localhost:8099/user/token/" + _ticket,
					dataType : "jsonp",
					type : "GET",
					success : function(data) {
						if (data.status == 200) {
							console.log(data)
							var username = data.data.username;
							var html = username
									+ "，欢迎来到淘淘！<a id='logout' class=\"link-logout\">[退出]</a>";
							$("#loginbar").html(html);
							$("#logout").click(function() {
								alert()
								$.ajax({
									dataType : "jsonp",
									url : "http://localhost:8099/user/logout/",
									data : {
										"token" : _ticket
									},
									success : function(data) {
										if (data.status == 200) {
											
										} else {
										}

									}

								})

							})
						}
					}
				});
	}
}

$(function() {
	// 查看是否已经登录，如果已经登录查询登录信息
	TT.checkLogin();
});