'use strict'
const functions = require('firebase-functions');
const admin=require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification=functions.database.ref('/Notifications/{uuid}/{notification_id}').onWrite((change,context)=>
{

	const uuid=context.params.uuid;
	const notification_id=context.params.notification_id;
	console.log('The Current uuid is',uuid);

	console.log('We have a notification to send to',uuid);

	if(!change.after.exists())
	{
		return console.log('New Notification has been deleted from database',notification_id);
	}
    

    const fromUser=admin.database().ref(`/Notifications/${uuid}/${notification_id}`).once('value');

    return fromUser.then(fromUserResult =>
    {

            const from_user_id=fromUserResult.val().From;
            console.log('you have new notification from',from_user_id);


             const userQuery= admin.database().ref(`/Users/${from_user_id}/Name`).once('value');

             return userQuery.then(userResult =>
                {
                            const userName=userResult.val();


                            const deviceToken=admin.database().ref(`/Users/${uuid}/DeviceToken`).once('value');

                            return deviceToken.then(result => 
                            {

                                const token_id=result.val();
                                const payload={
                                notification:{
                                    title: "New Friend Request",
                                    body: `${userName} has sent you friend request`,
                                    icon: "default",
                                    click_action: "com.example.jitesh.android_brill_training_task_2_TARGET_NOTIICATION"
                                },
                                data:{
                                    from_user_id : from_user_id
                                }
                            };
                            

                            return admin.messaging().sendToDevice(token_id,payload).then(response =>{

                                return console.log('This was the notification feature');
                            });


                           });



                });


            



    });

	
   


 

	});



	

