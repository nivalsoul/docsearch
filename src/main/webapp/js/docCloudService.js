//angular.module('docSearchApp2', [])
app.factory('docCloudService',
    function($http, $q) {

    return {
        saveDoc : function(token,data){
            return $http({
                method: 'POST',
                url: "/v1/docdive/documents",
                data:data,
                headers: {
                    'Content-Type': 'application/json',
                    'X-Auth-Token':token
                }
            })
        },
        loadFolder : function(token) {
            return $http({
                method: 'GET',
                url: "/v1/docdive/categories/0",
                headers: {
                    'Content-Type': 'application/json',
                    'X-Auth-Token':token
                }
            })
        },
        createFolder : function(token,data) {
            return $http({
                method: 'POST',
                url: "/v1/docdive/categories",
                data:data,
                headers: {
                    'Content-Type': 'application/json',
                    'X-Auth-Token':token
                }
            })
        },
        editFolder : function(token,id,data) {
            return $http({
                method: 'PUT',
                url: "/v1/docdive/categories/"+id,
                data:data,
                headers: {
                    'Content-Type': 'application/json',
                    'X-Auth-Token':token
                }
            })
        },
        delFolder : function(token,id) {
            return $http({
                method: 'DELETE',
                url: "/v1/docdive/categories/"+id,
                headers: {
                    'Content-Type': 'application/json',
                    'X-Auth-Token':token
                }
            })
        },
        loadDoc : function(token,id){
            return $http({
                method: 'GET',
                url: "/v1/docdive/categories/"+id,
                headers: {
                    'Content-Type': 'application/json',
                    'X-Auth-Token':token
                }
            })
        },
        loadTenants : function(token){
            return $http({
                method:'GET',
                url:"/v1/docdive/tenants",
                headers: {
                    'Content-Type': 'application/json',
                    'X-Auth-Token':token
                }
            })
        },
        delDoc : function(token,id) {
            return $http({
                method: 'DELETE',
                url: "/v1/docdive/documents/"+id,
                headers: {
                    'Content-Type': 'application/json',
                    'X-Auth-Token':token
                }
            })
        },
        editDoc : function(token,data){
            return $http({
                method: 'PUT',
                url: "/v1/docdive/documents/"+data._id,
                data:data,
                headers: {
                    'Content-Type': 'application/json',
                    'X-Auth-Token':token
                }
            })
        },
        delFile : function(token,id) {
            return $http({
                method: 'DELETE',
                url: "/v1/docdive/documents/file/"+id,
                headers: {
                    'Content-Type': 'application/json',
                    'X-Auth-Token':token
                }
            })
        },
        convertFile : function(token,id) {
            return $http({
                method: 'POST',
                url: "/v1/docdive/documents/file/format/"+id,
                headers: {
                    'Content-Type': 'application/json',
                    'X-Auth-Token':token
                }
            })
        },
        stats : function(token){
            return $http({
                method: 'GET',
                url: "/v1/docdive/documents/stats",
                headers: {
                    'Content-Type': 'application/json',
                    'X-Auth-Token':token
                }
            })
        },
        search : function(token,para){
            return $http({
                method: 'GET',
                url: "/v1/docdive/documents/search?"+para,
                headers: {
                    'Content-Type': 'application/json',
                    'X-Auth-Token':token
                }
            })
        },
        getDocument : function(token,documentId){
            return $http({
                method: 'GET',
                url: "/v1/docdive/documents/"+documentId,
                headers: {
                    'Content-Type': 'application/json',
                    'X-Auth-Token':token
                }
            })
        }
    };

});