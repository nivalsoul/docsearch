'use strict';
app.controller('loginCtrl', 
    function($scope,$window,$uibModalInstance) {
        var token="";  
        $scope.show_reg = true;
        
        $scope.login=function(){
        	var useraccount = $("#loginForm #useraccount").val();
        	var password = $("#loginForm #password").val();
        	$.post("users/user/login", {"email" : useraccount, "password" : password}, 
        		function(data, status) {
        			if(data.code==0){
        				//登录成功
        				$("#userProfile").show();
        				var useraccount = $.cookie("useraccount");
        				var username = $.cookie("username");
        				console.log(useraccount+username);
        				$("#un").html(username);
        				$("#ua").attr("href", "user/"+useraccount);
        				$uibModalInstance.close();
        			}else{
        				alert(data.info);
        			}
        		}
        	);
        };


        $scope.setFormEvent=function(){
        	//设置注册表单事件
        	$("#regForm").submit(function(){
        		var useraccount = $("#regForm #useraccount").val();
        		var username = $("#regForm #username").val();
        		var email = $("#regForm #email").val();
        		var password = $("#regForm #password").val();
        		$.post("users", {"useraccount" : useraccount, "username" : username,
        			"email" : email, "password" : password}, 
        			function(data, status) {
        				if(data=="ok"){
        					alert("注册成功！已自动登录。");
        					$("#un").html(username);
        					$("#ua").attr("href", "user/"+useraccount);
        					$uibModalInstance.close();
        				}else{
        					alert(data);
        				}
        			}
        		);
        		//阻止表单自动提交
        		return false;
        	});
        };
        


        $scope.logout=function(){
        	$.post("users/user/logout", {}, 
        		function(data, status) {
        			if(data=="ok"){
        				$("#un").html("");
        				$("#ua").attr("href", "#");
        				$.cookie("useraccount", null ,{path:"/"});
        				$.cookie("username", null ,{path:"/"});
        			}else{
        				alert(data);
        			}
        		}
        	);
        };
        

        $scope.toReg=function(){
        	$("#loginDiv").hide();
        	$("#regDiv").show();
        	$scope.setFormEvent();
        	$("#myModalLabel").html("用户注册");
        	$scope.show_reg=false;
        };

        $scope.toLogin=function(){
        	$("#loginDiv").show();
        	$("#regDiv").hide();
        	$("#myModalLabel").html("用户登录");
        	$scope.show_reg=true;
        };

    }
);