#!/bin/bash
stdbuf -o0 curl --user nicolas.payares@sas.com:Orion123 -s   https://api.relay42.com:443/v1/site-1252/profiles/interactions/stream -G --data-urlencode "query=interaction.interactionType==\"engagement\"" > C:/temp/test/nicotest4.txt &
exit 0

