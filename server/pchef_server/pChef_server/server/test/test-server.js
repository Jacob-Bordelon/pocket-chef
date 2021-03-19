const app = require("../server");
const chai = require("chai");
const chaiHttp = require("chai-http");
let should = chai.should();

const { expect } = chai;

// testing of REST API requests 
chai.use(chaiHttp);
describe("REST API tests", () => {
    describe("/possible POST request", () => {
        it("Expect id 1 from egg", done => {
            chai
            .request(app)
            .post("/possible")
            .send([{"Ingredient": "egg"}])
            .end((err, res) => {
                res.should.have.status(200)
                expect(JSON.parse(res.text)[0].id).to.be.eql(1);
                done();
            });
        });

        it("Expects type string and parsed to jsonarray", done => {
            chai
            .request(app)
            .post("/possible")
            .send([{"Ingredient": "egg"}])
            .end((err, res) => {
                res.should.have.status(200)
                res.text.should.be.a('string')
                JSON.parse(res.text).should.be.a('array')
                done();
            });
        });
    });
});