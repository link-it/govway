			<property name="originalSenderProperty" 
					key="originalSender" 
					datatype="string" 
					required="false">
				<value>
					<url pattern=".+originalSender=([^&amp;]*).*" />
					<header name="X-AS4-OriginalSender" />
				</value>
			</property>
			<property name="finalRecipientProperty" 
					key="finalRecipient" 
					datatype="string" 
					required="false">
				<value>
					<url pattern=".+finalRecipient=([^&amp;]*).*" />
					<header name="X-AS4-FinalRecipient" />
				</value>
			</property>

