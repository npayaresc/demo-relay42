stdbuf -oL curl --user nicolas.payares@sas.com:Orion123 -s   https://api.relay42.com:443/v1/site-1252/profiles/interactions/stream -G --data-urlencode "query=interaction.interactionType==\"engagement\"" >> c:/temp/test/nicotest4.txt
sleep -m 300
exit /b 0