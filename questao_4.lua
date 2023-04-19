function attack(pokemon)
    local random = math.random(1, 20)
    if(random <= 10 ) then
        return pokemon - choqueDoTrovao, "Choque Do Trovao"

    elseif (random > 10 and random <=15) then
        return pokemon - caudaDeFerro, "Cauda De Ferro"

    elseif (random > 15 and random <= 18) then
        return pokemon - investidaTrovao, "Investida Trovao"
    else
        return pokemon - trovao, "Trovao"
    end
end

pikachu = coroutine.create(function ()
    while (HPraichu > 0 and HPpikachu >0) do
        local ataqueNome
        HPraichu, ataqueNome = attack(HPraichu);
        print("Pikachu atacou com " .. ataqueNome)
        coroutine.yield(ataqueNome)

    end

end)

raichu = coroutine.create(function ()
    while (HPpikachu > 0 and HPraichu > 0) do
        local ataqueNome
        HPpikachu, ataqueNome = attack(HPpikachu);
        print("Raichu atacou com " .. ataqueNome)
        coroutine.yield(ataqueNome)

    end
end)

choqueDoTrovao = 50
caudaDeFerro = 100
investidaTrovao = 150
trovao = 200
HPpikachu = 800
HPraichu = 1000
local turno = 0

while (HPpikachu > 0 and HPraichu > 0) do
    turno = turno + 1
    print("\nTurno "..turno..": \n")
    print("Pikachu ataca")
    coroutine.resume(pikachu)
    print("HP Pikachu: ", HPpikachu)
    print("HP Raichu: ", HPraichu)

    if(HPraichu >0)then
        print("\n")
        print("Raichu ataca")
        coroutine.resume(raichu)
        print("HP Pikachu: ", HPpikachu)
        print("HP Raichu: ", HPraichu)
    end
end
if (HPpikachu <= 0) then
    print("\nPikachu perdeu")
elseif (HPraichu <= 0) then
    print("\nRaichu perdeu")
end


